#!/usr/bin/sh

usage ()
{
cat << EOF

Greps all generated SQL files for the passed parameter, and outputs the stuff
that's relevant. The statements are identified by customized grep for the
individual create-files. Simple grep does not work!

=> Script expects to be run in logon-persistence <=

Parameters:
1: full table name, case-IN-sensitive, e.g. TLGNC_LOG

Output:
- Relevant SQL fragments

Examples:
$0 TLGNC_PDA_DEVICE : all SQL belonging to this table
$0 tlgnr_lang_bs    : detects a refdata table! -> _BS and _TX

The data is always displayed as CREATE (it's only a bunch of grep's...),
but it also contains hints on what to do for PATCH scripts, e.g.:

- CREATE Tables:
  The output is normally save to execute 1:1 in SQL.
  
- ALTER Tables:
  You have to manually transform the CREATE into ALTER, and
  pick the fragments you need (columns, FK, Index, comments)

Limitations:
- Identifying Sequences does NOT work for names which must be truncated by Generator!

  The major limitation is for tables which have a long tablename,
  where the name-mangling for the generated sequence kicks in.

EOF
}

GENCREATEDIR=./build/generated/gdi/adxml2ddl/sql/create

TABLENAME=`echo $1 | tr "[:lower:]" "[:upper:]"`
if [ "$TABLENAME" == "" ] ; then
  usage;
  exit;
fi

TABLENAME_LOWER=`echo $TABLENAME | tr "[:upper:]" "[:lower:]"`

TABLENAME_PREFIX=`echo "$TABLENAME" | head -c5`
echo "-- (Debug) Prefix:   $TABLENAME_PREFIX"
if [ "$TABLENAME_PREFIX" == "TLGNR" ] ; then
  TABLENAME_TX="`echo "$TABLENAME" | head -c-4`_TX"
  TABLENAME_BS="`echo "$TABLENAME" | head -c-4`_BS"
  echo "-- Using BS: $TABLENAME_BS"
  echo "-- Using TX: $TABLENAME_TX"
fi

VIEWNAME=`echo "$TABLENAME" | sed -e "s/TLGNR_/VLGNR_/" | head -c-4`

echo
echo -- 10_gen_create_tables.sql.
echo "-- New tables:     PATCH: include. DEV DB: Execute manually."
echo "-- Exiting tables: PATCH: Turn manually into ALTER TABLE commands. DEV DB: Execute THOSE manually."
TABLE_FROM=`grep -n "$TABLENAME_LOWER " $GENCREATEDIR/10_gen_create_tables.sql | awk -F":" {'print $1'} | head -1`
TABLE_NUMLINES=`tail -n +$TABLE_FROM $GENCREATEDIR/10_gen_create_tables.sql | grep -n ";" | head -1 | awk -F":" {'print $1'}`
echo -- Line $TABLE_FROM, next $TABLE_NUMLINES lines
if [ "$TABLE_FROM" != "" ] ; then
  tail -n +$TABLE_FROM $GENCREATEDIR/10_gen_create_tables.sql | head -$TABLE_NUMLINES
fi

echo
echo -- 15_gen_create_indexes.sql.
echo "-- PATCH: No need to add explicitely (are always called). DEV DB: Execute manually though!"
grep "$TABLENAME " $GENCREATEDIR/15_gen_create_indexes.sql

echo
echo -- 20_gen_create_constraints_pk.sql.
echo "-- PATCH: include. DEV DB: Execute manually though!"
grep "$TABLENAME " $GENCREATEDIR/20_gen_create_constraints_pk.sql

echo
echo -- 22_gen_create_constraints_unique.sql
echo "-- PATCH: No need to add explicitely (are always called). DEV DB: Execute manually though!"
grep "$TABLENAME " $GENCREATEDIR/22_gen_create_constraints_unique.sql

echo
echo -- 24_gen_create_constraints_fk.sql
echo "-- PATCH: Add for new columns (or new tables). DEV DB: Execute manually."
grep -E -A4 "ALTER TABLE         $TABLENAME\$" $GENCREATEDIR/24_gen_create_constraints_fk.sql

echo
echo -- 26_gen_create_constraints_fk_index.sql
echo "-- PATCH: No need to add explicitely (are always called). DEV DB: Execute manually though!"
grep "$TABLENAME " $GENCREATEDIR/26_gen_create_constraints_fk_index.sql

echo
echo "-- 28_gen_create_sequences.sql - If Empty: VERIFY MANUALLY (potentially name mangling issues because of 30 character restriction)"
echo "-- PATCH: Add for new tables. DEV DB: Execute manually."
SEQNAME=`echo $TABLENAME | sed -e "s|^TLGN|SLGN|"`
grep -i "$SEQNAME " $GENCREATEDIR/28_gen_create_sequences.sql

#30_gen_create_text_views is no longer generated. This difficult lookup no longer needed...
#
#if [ "$TABLENAME" == "$TABLENAME_TX" ] ; then
#  echo -- 30_gen_create_text_views.sql - $VIEWNAME
#  VIEW_FROM=`grep -n "$VIEWNAME " $GENCREATEDIR/30_gen_create_text_views.sql | awk -F":" {'print $1'} | head -1`
#  VIEW_NUMLINES=`tail -n +$VIEW_FROM $GENCREATEDIR/30_gen_create_text_views.sql | grep -n "WHERE" | head -1 | awk -F":" {'print $1'}`
#  echo -- Line $VIEW_FROM, next $VIEW_NUMLINES lines
#  tail -n +$VIEW_FROM $GENCREATEDIR/30_gen_create_text_views.sql | head -$VIEW_NUMLINES
#else
#  echo -- 30_gen_create_text_views.sql - not checked, not a _TX table
#fi

echo
echo -- 40_gen_create_comments.sql
echo "-- PATCH: No need to add (are always called). DEV DB: Execute manually."
grep "TABLE     $TABLENAME " $GENCREATEDIR/40_gen_create_comments.sql
grep "COLUMN    $TABLENAME\." $GENCREATEDIR/40_gen_create_comments.sql

echo
echo -- 45_gen_grant_privileges.sql
echo "-- PATCH: No need to add (are always called). DEV DB: Execute manually though."
grep "$TABLENAME " $GENCREATEDIR/45_gen_grant_privileges.sql
grep "$SEQNAME " $GENCREATEDIR/45_gen_grant_privileges.sql

echo
echo -- 47_gen_insert_tablemeta.sql
echo "-- PATCH: No need to add (are always called). DEV DB: Execute manually though."
grep "$TABLENAME" $GENCREATEDIR/47_gen_insert_tablemeta.sql

# If called with BS, output BS and call script again with TX. Therefore, if called with TX, only show TX
if [ "$TABLENAME" == "$TABLENAME_BS" ] ; then
  echo "-- Calling recursively for $TABLENAME_TX with $0"
  $0 $TABLENAME_TX
fi

exit;
