#!/bin/bash

SCRIPT=`basename $0`
SCRIPTDIR="$(cd `dirname $0` && pwd)"
OUTPUT=/dev/stdout
SAP_ONLY=0
ADD_SUBPATCH=0

# Use the new template by default
RECREATE_INDEXES_AND_CONSTRAINTS=0
# Do not ignore not existing drop scripts by default (in case of the old template)
FORCEMODE=0

# Prints usage when script is called with the -h switch.
function usage() {
    echo "Usage: `basename $0` [options]"
    echo
    echo "The script will guide you interactively through the process"
    echo "of creating a patch."
    echo
    echo "Options:"
    echo "-h    Display this help"
    echo "-s    Create sap patch only"
    echo "-r    Recreate objects around the DB patch, which means: use the old template."
    echo "      This will drop first the indexes and constraints, execute the patch"
    echo "      and then finally create the indexes and constraints again."
    echo "-f    Force mode -> create patch even if the drop files from the"
    echo "      'from' versions do not exist yet. This may be useful if there"
    echo "      is ongoing work in a branch on a patch that has not been released."
    echo "      Please notice that force mode is only useful together with the -r option,"
    echo "      because otherwise the new template is used which won't use the drop scripts anyway."
    echo "-a    Add a subpatch to an existing patch"
    echo "      This shall be used when part of the production DB patch should not run on INT, LQ or LT environments"
    echo "      which happens every time when a go-live of a patched version was canceled after quality testing"
}

function inject_subpatch() {
  echo "Injecting subpatch into '$ORIGINAL_PATCHFILE'..."
  sed -i "s/l_version_without_rc = \('.*'\))/l_version_without_rc = \1, '$TO_VERSION')/" "$PATCHDIR/$ORIGINAL_PATCHFILE"
  TEMP_FILE=$(mktemp)
  sed $TEMPLATE_CALL_SUBPATCH \
      -e 's/__FROM_VERSION__/'"$FROM_VERSION"'/g' \
      -e 's/__TO_VERSION__/'"$TO_VERSION"'/g' \
      -e 's/__BUILD_SEQUENCE__/'"$BUILD_SEQUENCE"'/g' \
      -e 's|__ORIGINAL_PATCHFILE__|'"${ORIGINAL_PATCHFILE%.sql}"'|g' \
      > $TEMP_FILE
  sed -i "/-- Patching the entities for the main schema/ r $TEMP_FILE" "$PATCHDIR/$ORIGINAL_PATCHFILE"
  rm -f $TEMP_FILE
  echo "Subpatch call injected into '$ORIGINAL_PATCHFILE'"
}

function create_subpatch() {
  echo "Creating subpatch for '$ORIGINAL_PATCHFILE'..."
  mkdir -p $PATCHDIR
  sed $TEMPLATE_SUBPATCH \
      -e 's/__FROM_VERSION__/'"$FROM_VERSION"'/g' \
      -e 's/__TO_VERSION__/'"$TO_VERSION"'/g' \
      -e 's|__ORIGINAL_PATCHFILE__|'"$ORIGINAL_PATCHFILE"'|g' \
      > $PATCHDIR/$SUBPATCHFILE
  sed $TEMPLATE_SUBPATCH_SKIPPED \
      -e 's/__FROM_VERSION__/'"$FROM_VERSION"'/g' \
      -e 's/__TO_VERSION__/'"$TO_VERSION"'/g' \
      -e 's|__ORIGINAL_PATCHFILE__|'"$ORIGINAL_PATCHFILE"'|g' \
      > $PATCHDIR/$SUBPATCHFILE_SKIPPED
  echo "Subpatch created:"
  echo "  '$PATCHDIR/$SUBPATCHFILE'"
  echo "  '$PATCHDIR/$SUBPATCHFILE_SKIPPED'"
}

function create_sap_patch() {
  mkdir -p $PATCHDIR
  sed $TEMPLATE_SAP \
      -e 's/__FROM_VERSION__/'"$FROM_VERSION"'/g' \
      -e 's/__TO_VERSION__/'"$TO_VERSION"'/g' \
      -e 's/__BUILD_SEQUENCE__/'"$BUILD_SEQUENCE"'/g' \
      > $PATCHDIR/$PATCHFILE_SAP
  echo "sap patch created: $PATCHDIR/$PATCHFILE_SAP"
}

# Checks whether the drop scripts with the given 'from version' can be found
# in the shared folder of the released LogonDB package.
function check_drop_scripts() {
  # The directory containing drop scripts and all the required files must exist
  if [ ! -d "$GENDROPDIR" ] && [ $FORCEMODE == 0 ]; then
    echo "Directory containing generated drop scripts of the 'from version'"
    echo "is missing. Cannot find"
    echo "$GENDROPDIR".
    exit 1
    echo "Aborting."
  elif [ ! -d "$GENDROPDIR" ]; then
    echo "Warning, directory $GENDROPDIR containing generated drop scripts"
    echo "of the starting DB version is missing, skipping copying of drop files."
  else
    for f in $DROPFILES; do
      SRC="$GENDROPDIR"/"$f"
      if [ ! -f "$SRC" ] && [ $FORCEMODE == 0]; then
        echo "Generated file $SRC does not exist, aborting."
        exit 1
      elif [ ! -f "$SRC" ]; then
        echo "Warning, generated file $SRC does not exist, can't copy to patch directory."
      fi
    done
  fi
}

# Copies the drop scripts with the given 'from version'
# from the shared folder of the released LogonDB package.
function copy_drop_scripts() {
  echo "Copying drop scripts from HEAD"
  if [ -d "$GENDROPDIR" ]; then
    for f in $DROPFILES; do
      SRC="$GENDROPDIR"/"$f"

      if [ $FORCEMODE == 1 ] && [ ! -f "$SRC" ]; then
        # In force mode, we ignore nonexisting files.
        continue
      fi

      if [ -f "$PATCHDIR"/"$f" ]; then
        echo "File $PATCHDIR/$f already exists, skipping."
      else
        cp -v "$SRC" "$PATCHDIR"
      fi
    done
  fi
}

while getopts "hvo:afrs" opt; do
  case $opt in
    h)
      usage
      exit 1
    ;;
    v)
      VERBOSE=1
    ;;
    f)
      FORCEMODE=1
    ;;
    o)
      OUTPUT=$OPTARG
    ;;
    r)
      RECREATE_INDEXES_AND_CONSTRAINTS=1
    ;;
    s)
      SAP_ONLY=1
    ;;
     a)
      ADD_SUBPATCH=1
      ;;
    *)
      usage
      exit 1
    ;;
  esac
done

shift $((OPTIND-1))

if [ $FORCEMODE == 1 ]; then
  if [ $RECREATE_INDEXES_AND_CONSTRAINTS == 1 ]; then
    echo "Running in force mode"
  else
    echo "WARNING: Force mode has no effect with the new template!!!"
    echo "(Since it won't use the drop scripts anyway.)"
    echo ""
  fi
fi

TEMPLATE="templates/patch_x.x.x_to_y.y.y.sql"
TEMPLATE_SAP="templates/patch_sap_x.x.x_to_y.y.y.sql"
TEMPLATE_SUBPATCH="templates/subpatch_x.x.x_to_y.y.y.sql"
TEMPLATE_SUBPATCH_SKIPPED="templates/subpatch_x.x.x_to_y.y.y_skipped.sql"
TEMPLATE_CALL_SUBPATCH="templates/call_subpatch.sql"
UPDTDTPL="templates/update_adnovum_initial_and_testdata.sql"

if [ ! -f "$TEMPLATE" ]; then
  echo "Can't find patch template file $TEMPLATE, did you delete it?"
  exit 1
fi

if [ ! -f "$UPDTDTPL" ]; then
  echo "Can't find patch template file $UPDTDTPL, did you delete it?"
  exit 1
fi

if [ $ADD_SUBPATCH == 1 ]; then
  echo ============================================================
  echo Adding a subpatch for existing DB patch
  echo ============================================================
else
  echo ============================================================
  echo Creating a new DB patch from the template
  echo ============================================================
fi

echo "Type the DB version from which the patch should start upgrade the DB"
echo "that you want to patch [e.g. 2.3.0]:"
read FROM_VERSION

if [ -z "$FROM_VERSION" ]; then
  echo "The version cannot be empty, aborting."
  exit 1;
fi

if [ $RECREATE_INDEXES_AND_CONSTRAINTS == 1 ] && [ $ADD_SUBPATCH == 0 ]; then
  echo "Type the optional build sequence of the version currently installed in production"
  echo "For example '0' for the first build of '$FROM_VERSION'"
  read BUILD_SEQUENCE
fi

echo "Type the target version to install"
echo "[e.g. 2.3.0]:"
read TO_VERSION

if [ -z "$TO_VERSION" ]; then
  echo "The version cannot be empty, aborting."
  exit 1;
fi

# Decide the filename of patch file
if [ $RECREATE_INDEXES_AND_CONSTRAINTS == 1 ]; then
  echo "Using the old template, with which the patch will recreate indexes and constraints..."

  # Drop scripts to copy
  DROPFILES="60_gen_drop_constraints_fk_index.sql \
    64_gen_drop_constraints_unique.sql \
    70_gen_drop_indexes.sql"

  # Directory containing the *drop* scripts corresponding to the 'from version'
  GENDROPDIR="/share/install/logon/$FROM_VERSION$BUILD_SEQUENCE/logon/logon-persistence/build/generated/gdi/adxml2ddl/sql/drop"

  check_drop_scripts;

  PATCHFILE="patch_${FROM_VERSION}_to_${TO_VERSION}_and_recreate_indexes_and_constraints.sql"
else
  PATCHFILE="patch_${FROM_VERSION}_to_${TO_VERSION}.sql"
fi

# Filename of sap patch file
PATCHFILE_SAP="patch_sap_${FROM_VERSION}_to_${TO_VERSION}.sql"

# Filename of subpatch file
SUBPATCHFILE="subpatch_${FROM_VERSION}_to_${TO_VERSION}.sql"
SUBPATCHFILE_SKIPPED="subpatch_${FROM_VERSION}_to_${TO_VERSION}_skipped.sql"

PATCHDIR="from_$FROM_VERSION"

ORIGINAL_PATCHFILE=$(find "$PATCHDIR" -type f -name "patch_[0-9]*.sql")
ORIGINAL_PATCHFILE=${ORIGINAL_PATCHFILE##*/}

if [ $SAP_ONLY == 1 ]; then
  create_sap_patch
  exit 0
elif [ $ADD_SUBPATCH == 1 ]; then
  if [ ! -f "$PATCHDIR/$ORIGINAL_PATCHFILE" ]; then
    echo "Original patchfile in '$PATCHDIR/' does not exist, aborting."
    exit 1
  fi
  create_subpatch;
  inject_subpatch;
  exit 0;
fi

echo "You are about to create patch file $PATCHFILE. Continue? [y/n]"
read CONFIRM

if [ ! "$CONFIRM" = "y" ] && [ ! "$CONFIRM" = "Y" ]; then
  echo "Cancelled, aborting."
  exit 1;
fi

if [ -d "$PATCHDIR" ]; then
  echo "Directory $PATCHDIR already exists, creating new patch file there."
else
  echo "Creating directory $PATCHDIR."
  mkdir "$PATCHDIR"
fi

if [ -f "$PATCHDIR"/"$PATCHFILE" ]; then
  echo "File $PATCHDIR/$PATCHFILE already exists, aborting."
  exit 1
fi

# Copy patch file with substitutions
echo "Copying template $TEMPLATE to $PATCHDIR/$PATCHFILE"
cat "$TEMPLATE" | sed 's/__FROM_VERSION__/'"$FROM_VERSION"'/g' \
  | sed 's/__TO_VERSION__/'"$TO_VERSION"'/g' \
  | sed 's/__BUILD_SEQUENCE__/'"$BUILD_SEQUENCE"'/g' > "$PATCHDIR"/"$PATCHFILE"

if [ $RECREATE_INDEXES_AND_CONSTRAINTS == 1 ]; then
  copy_drop_scripts;
fi

UPDFILE=`basename "$UPDTDTPL"` # Want only part after last directory separator
if [ ! -f "$PATCHDIR"/"$UPDFILE" ]; then
  echo "Copying testdata update script template"
  cp -v "$UPDTDTPL" "$PATCHDIR"
else
  echo "Testdata update script $PATCHDIR/$UPDFILE already exists."
fi

echo
echo ====================================================================
echo "Successfully created patch $PATCHFILE in $PATCHDIR."
echo "Attention: Verifying correctness is your responsibility."
echo ====================================================================

echo "Wanna patch up sap too? [y/n]"
read INPUT
if [ ! "$INPUT" = "y" ] && [ ! "$INPUT" = "Y" ]; then
  echo "all done then"
  exit 0
else
  create_sap_patch
  echo -e "\nprompt Please remember to run $PATCHFILE_SAP as 'ulgnsap' if you haven't done so yet!" >> $PATCHDIR/$PATCHFILE
fi
