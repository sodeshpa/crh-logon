--------------------------------------------------------------------------------
--
-- PROJECT      : LOGON
--
-- AUTHOR       : AdNovum Informatik AG
-- DESCRIPTION  : UoM conversion routines which were required for migrating
--                restricted quantity contracts over to the new virtual
--                group leader model used by advance payment (LRQ 303.0).
--                The routines were taken from UomConverterServiceBean with
--                some slight modification when calling semantics as well as
--                only support for non-fuzzy UoM.
--
--------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE logon_uom AS

  FUNCTION ConvertQtyFromUoMToUoM(
    p_quantity        in NUMBER,
    p_from_uom_cd     in VARCHAR2,
    p_to_uom_cd       in VARCHAR2,
    p_sap_material_id in NUMBER) RETURN NUMBER;

END logon_uom;
/

CREATE OR REPLACE PACKAGE BODY logon_uom AS

  c_SCALE constant NUMBER := 4; -- 4 digits after decimal place

  -----------------------------------------------------------------------------
  -- FUNCTION ConvertQty
  --
  -- Computes a new quantity with the given conversion factor
  --
  -- IN:
  --  p_quantity    : quantity to convert
  --  p_numerator   : part of conversion factor
  --  p_denominator : part of conversion factor
  --  p_forward     : true for conversion from standard UoM to dimension, or from
  --                  alternative UoM to base UoM, false for backwards conversion
  --
  -- RETURN:
  --  The converted quantity rounded up and scaled to 4 decimal digits
  -----------------------------------------------------------------------------
  FUNCTION ConvertQty(
    p_quantity    in NUMBER,
    p_numerator   in NUMBER,
    p_denominator in NUMBER,
    p_forward     in BOOLEAN) RETURN NUMBER
  IS
    l_numerator     NUMBER  := p_numerator;
    l_denominator   NUMBER  := p_denominator;
    r_converted_qty NUMBER;
  BEGIN
    IF (NOT p_forward) THEN
      l_numerator := p_denominator;
      l_denominator := p_numerator;
    END IF;

    IF (p_numerator <> p_denominator) THEN
      r_converted_qty := p_quantity * l_numerator / l_denominator;
      r_converted_qty := ROUND(r_converted_qty, c_SCALE);
    ELSE
      r_converted_qty := p_quantity;
    END IF;

    RETURN r_converted_qty;
  END;


  -----------------------------------------------------------------------------
  -- FUNCTION GetDimensionCd
  --
  -- Convenience method that returns the dimension code of a standard UoM
  -- because these UoMs must have a dimension unlike alternative UoMs.
  --
  -- IN:
  --  p_uom_cd  : primary key for a standard UoM (not an alternative UoM)
  --              for a material
  --
  -- RETURN:
  --  The UoMDimension primary key for the given UoM. This method will only
  --  return NULL if the UoMDimension for the given UoM is NULL in the
  --  database. Otherwise, it should always find a valid UoM entry.
  -----------------------------------------------------------------------------
  FUNCTION GetDimensionCd(p_uom_cd in VARCHAR2) RETURN VARCHAR2
  IS
    r_uom_dim_cd VARCHAR2(3);
  BEGIN
    SELECT uom.uom_dimension_cd
    INTO r_uom_dim_cd
    FROM tlgnr_uom_bs uom
    WHERE uom.uom_cd=p_uom_cd;

    RETURN r_uom_dim_cd;
  END;


  -----------------------------------------------------------------------------
  -- FUNCTION FetchAltUom
  --
  -- Convenience method to find the alternative UoM for the given material
  -- and base UoM.
  --
  -- IN:
  --  p_uom_cd          : primary key for UoM in question
  --  p_sap_material_id : primary key for material
  --
  -- RETURN:
  --  The primary key of the TLGNS_SAP_MATERIAL_ALT_UOM corresponding to the
  --  given inputs or NULL if no mapping was found.
  -----------------------------------------------------------------------------
  FUNCTION FetchAltUom(
    p_uom_cd          in VARCHAR2,
    p_sap_material_id in NUMBER) RETURN NUMBER
  IS
    r_alt_uom_id NUMBER;
  BEGIN
    SELECT alt.sap_material_alt_uom_id
    INTO r_alt_uom_id
    FROM tlgns_sap_material_alt_uom alt
    WHERE alt.sap_material_id=p_sap_material_id
    AND alt.uom_cd=p_uom_cd;

    RETURN r_alt_uom_id;

  EXCEPTION
    WHEN no_data_found THEN
      RETURN NULL;
  END;


  -----------------------------------------------------------------------------
  -- PROCEDURE ConvertBetweenUomAndDimension
  --
  -- Converts the given quantity using the numerator and denominator defined
  -- for the given UoM. Furthermore, it updates the current running
  -- numerator and denominator values.
  --
  -- The IN OUT parameters here represent the updating that the Java code
  -- does directly on the UomConversionResult object.
  --
  -- IN:
  --  p_uom_cd          : primary key for UoM
  --  p_forward         : true for conversion from standard UoM to dimension
  --                      or from alternative UoM to base UoM, false for
  --                      backwards conversion
  --
  -- IN OUT:
  --  p_io_quantity     : the quantity to update
  --  p_io_numerator    : currently running numerator used in all conversions
  --  p_io_denominator  : currently running denominator used in all conversions
  -----------------------------------------------------------------------------
  PROCEDURE ConvertBetweenUomAndDimension(
    p_uom_cd          in VARCHAR2,
    p_io_quantity     in out NUMBER,
    p_io_numerator    in out NUMBER,
    p_io_denominator  in out NUMBER,
    p_forward         in BOOLEAN)
  IS
    l_uom_numerator   NUMBER;
    l_uom_denominator NUMBER;
  BEGIN
    SELECT uom.numerator_for_conversion, uom.denominator_for_conversion
    INTO l_uom_numerator, l_uom_denominator
    FROM tlgnr_uom_bs uom
    WHERE uom.uom_cd=p_uom_cd;

    p_io_quantity := ConvertQty(p_io_quantity, l_uom_numerator, l_uom_denominator, p_forward);

    p_io_numerator   := p_io_numerator * l_uom_numerator;
    p_io_denominator := p_io_denominator * l_uom_denominator;
  END;


  -----------------------------------------------------------------------------
  -- PROCEDURE ConvertBetweenAltUomAndBaseUom
  --
  -- Converts the given quantity using the numerator and denominator defined
  -- for the given alternative UoM. Furthermore, it updates the current running
  -- numerator and denominator values.
  --
  -- The IN OUT parameters here represent the updating that the Java code
  -- does directly on the UomConversionResult object.
  --
  -- IN:
  --  p_alt_uom_id      : primary key for alternative UoM
  --  p_forward         : true for conversion from standard UoM to dimension
  --                      or from alternative UoM to base UoM, false for
  --                      backwards conversion
  --
  -- IN OUT:
  --  p_io_quantity     : the quantity to update
  --  p_io_numerator    : currently running numerator used in all conversions
  --  p_io_denominator  : currently running denominator used in all conversions
  -----------------------------------------------------------------------------
  PROCEDURE ConvertBetweenAltUomAndBaseUom(
    p_alt_uom_id      in NUMBER,
    p_io_quantity     in out NUMBER,
    p_io_numerator    in out NUMBER,
    p_io_denominator  in out NUMBER,
    p_forward         in BOOLEAN)
  IS
    l_uom_numerator   NUMBER;
    l_uom_denominator NUMBER;
  BEGIN
    SELECT alt.numerator_for_conversion, alt.denominator_for_conversion
    INTO l_uom_numerator, l_uom_denominator
    FROM tlgns_sap_material_alt_uom alt
    WHERE alt.sap_material_alt_uom_id=p_alt_uom_id;

    p_io_quantity := ConvertQty(p_io_quantity, l_uom_numerator, l_uom_denominator, p_forward);

    p_io_numerator   := p_io_numerator * l_uom_numerator;
    p_io_denominator := p_io_denominator * l_uom_denominator;
  END;


  -----------------------------------------------------------------------------
  -- PROCEDURE FindAltOrBaseUomWithCommonDim
  --
  -- Find a base UoM or alternative UoM that matches the given dimension to
  -- use for converting between the standard UoM and the material-specific
  -- UoM.
  --
  -- Depending on the match, this method will set one of the following OUT
  -- parameters:
  --
  -- If the material's base UoM matches the target UoM, then p_uom_cd_out is
  -- set and p_sap_material_alt_uom_id_out is set to NULL. However, if no
  -- match is found, the p_uom_cd_out is set to NULL and
  -- p_sap_material_alt_uom_id_out is set to the primary key corresponding
  -- to the material's alternative UoM mapping.
  --
  -- IN:
  --  p_sap_material_id : primary key for material
  --  p_base_uom_cd     : primary key for material's UoM
  --  p_target_uom_cd   : primary key for target UoM
  --
  -- OUT:
  --  p_uom_cd_out      : set to the primary key of the UoM to use iff the
  --                      material's UoM dimension matches the given target
  --                      UoM dimension
  --  p_sap_material_alt_uom_id_out: the primary key of the material's
  --        alternative UoM iff the material's UoM dimension does not match
  --        the target's UoM dimension
  -----------------------------------------------------------------------------
  PROCEDURE FindAltOrBaseUomWithCommonDim(
    p_sap_material_id             IN NUMBER,
    p_base_uom_cd                 IN VARCHAR2,
    p_target_uom_cd               IN VARCHAR2,
    p_uom_cd_out                  OUT VARCHAR2,
    p_sap_material_alt_uom_id_out OUT NUMBER)
  IS
    l_base_uom_dim_cd   VARCHAR2(3);
    l_target_uom_dim_cd VARCHAR2(3);
  BEGIN
    -- Sane defaults
    p_sap_material_alt_uom_id_out := NULL;
    p_uom_cd_out := NULL;

    l_target_uom_dim_cd := GetDimensionCd(p_target_uom_cd);

    -- Check if the base UoM of the material matches the desired dimension.
    l_base_uom_dim_cd := GetDimensionCd(p_base_uom_cd);
    IF (l_base_uom_dim_cd IS NOT NULL) THEN
      IF (l_base_uom_dim_cd = l_target_uom_dim_cd) THEN
        p_uom_cd_out := p_base_uom_cd;
        p_sap_material_alt_uom_id_out := NULL;
        RETURN;
      END IF;
    END IF;

    -- Find an alternative UoM that can still match the desired dimension.
    SELECT alt.sap_material_alt_uom_id, uom.uom_cd
    INTO p_sap_material_alt_uom_id_out, p_uom_cd_out
    FROM tlgns_sap_material_alt_uom alt
    JOIN tlgnr_uom_bs uom ON (uom.uom_cd=alt.uom_cd)
    WHERE alt.sap_material_id=p_sap_material_id
    AND uom.uom_dimension_cd=l_target_uom_dim_cd;
  END;


  -----------------------------------------------------------------------------
  -- FUNCTION ConvertQtyFromUoMToUoM
  --
  -- Convert's the given quantity in the given base UoM to the target UoM
  --
  -- IN:
  --  p_quantity        : the quantity to convert given in the from UoM
  --  p_from_uom_cd     : primary key for starting UoM
  --  p_to_uom_cd       : primary key for the target UoM
  --  p_sap_material_id : primary key for material
  --
  -- RETURN:
  --  The converted quantity scaled to 4 decimal digits
  -----------------------------------------------------------------------------
  FUNCTION ConvertQtyFromUoMToUoM(
    p_quantity        IN NUMBER,
    p_from_uom_cd     IN VARCHAR2,
    p_to_uom_cd       IN VARCHAR2,
    p_sap_material_id IN NUMBER) RETURN NUMBER
  IS
    r_quantity        NUMBER := p_quantity;
    l_numerator       NUMBER := 1;
    l_denominator     NUMBER := 1;
    l_base_uom_cd     VARCHAR2(3);
    l_to_uom_dim_cd   VARCHAR2(3);
    l_from_uom_dim_cd VARCHAR2(3);
    l_to_alt_uom_id   NUMBER;
    l_from_alt_uom_id NUMBER;
    l_alt_uom_cd      VARCHAR2(3);
    l_alt_uom_id      NUMBER;

    conv_error        EXCEPTION;

  BEGIN
    IF (p_quantity = 0 OR p_quantity IS NULL) THEN
      RETURN 0;
    END IF;
    
    IF (p_from_uom_cd = p_to_uom_cd) THEN
      RETURN p_quantity;
    END IF;

    SELECT mat.base_uom_cd
    INTO l_base_uom_cd
    FROM tlgns_sap_material mat
    WHERE mat.sap_material_id=p_sap_material_id;

    IF (p_from_uom_cd = l_base_uom_cd) THEN
      l_to_alt_uom_id := FetchAltUom(p_to_uom_cd, p_sap_material_id);

      IF (l_to_alt_uom_id IS NOT NULL) THEN
        -- Base => Alt
        ConvertBetweenAltUomAndBaseUom( l_to_alt_uom_id, r_quantity, l_numerator, l_denominator, false);
      ELSE
        -- Base => Std
        FindAltOrBaseUomWithCommonDim( p_sap_material_id, l_base_uom_cd, p_to_uom_cd, l_alt_uom_cd, l_alt_uom_id);

        IF (l_alt_uom_id IS NULL) THEN
          -- Base => Dimension
          ConvertBetweenUomAndDimension( p_from_uom_cd, r_quantity, l_numerator, l_denominator, true);
        ELSE
          -- Base => Alt
          ConvertBetweenAltUomAndBaseUom( l_alt_uom_id, r_quantity, l_numerator, l_denominator, false);
          -- Alt => Dimension
          ConvertBetweenUomAndDimension( l_alt_uom_cd, r_quantity, l_numerator, l_denominator, true);
        END IF;

        -- Dimension => Std
        ConvertBetweenUomAndDimension( p_to_uom_cd, r_quantity, l_numerator, l_denominator, false);
      END IF;
    ELSE
      -- p_from_uom_cd <> l_base_uom_cd
      l_from_alt_uom_id := FetchAltUom(p_from_uom_cd, p_sap_material_id);

      IF (l_from_alt_uom_id IS NOT NULL) THEN
        IF (p_to_uom_cd = l_base_uom_cd) THEN
          -- Alt => Base
          ConvertBetweenAltUomAndBaseUom( l_from_alt_uom_id, r_quantity, l_numerator, l_denominator, true);
        ELSE
          l_to_alt_uom_id := FetchAltUom(p_to_uom_cd, p_sap_material_id);

          IF (l_to_alt_uom_id IS NOT NULL) THEN
            -- Alt => Alt
            -- Alt => Base
            ConvertBetweenAltUomAndBaseUom( l_from_alt_uom_id, r_quantity, l_numerator, l_denominator, true);
            -- Base => Alt
            ConvertBetweenAltUomAndBaseUom( l_to_alt_uom_id, r_quantity, l_numerator, l_denominator, false);
          ELSE
            -- Alt => Std

            -- Alt => Base
            ConvertBetweenAltUomAndBaseUom( l_from_alt_uom_id, r_quantity, l_numerator, l_denominator, true);

            FindAltOrBaseUomWithCommonDim( p_sap_material_id, l_base_uom_cd, p_to_uom_cd, l_alt_uom_cd, l_alt_uom_id);
            IF (l_alt_uom_id IS NULL) THEN
              -- Base => Dimension
              ConvertBetweenUomAndDimension( l_base_uom_cd, r_quantity, l_numerator, l_denominator, true);
            ELSE
              -- Base => Other Alt
              ConvertBetweenAltUomAndBaseUom( l_alt_uom_id, r_quantity, l_numerator, l_denominator, false);
              -- Other Alt => Dimension
              ConvertBetweenUomAndDimension( l_alt_uom_cd, r_quantity, l_numerator, l_denominator, true);
            END IF;

            -- Dimension => Std
            ConvertBetweenUomAndDimension( p_to_uom_cd, r_quantity, l_numerator, l_denominator, false);
          END IF;
        END IF;
      ELSE
        -- l_from_alt_uom_id IS NULL

        -- Std => Dimension
        ConvertBetweenUomAndDimension( p_from_uom_cd, r_quantity, l_numerator, l_denominator, true);
        IF (p_to_uom_cd = l_base_uom_cd) THEN
          -- Std => Base
          FindAltOrBaseUomWithCommonDim( p_sap_material_id, l_base_uom_cd, p_from_uom_cd, l_alt_uom_cd, l_alt_uom_id);
          IF (l_alt_uom_id IS NULL) THEN
            -- Dimension => Base
            ConvertBetweenUomAndDimension( p_to_uom_cd, r_quantity, l_numerator, l_denominator, false);
          ELSE
            -- Dimension => Alt
            ConvertBetweenUomAndDimension( l_alt_uom_cd, r_quantity, l_numerator, l_denominator, false);
            -- Alt => Base
            ConvertBetweenAltUomAndBaseUom( l_alt_uom_id, r_quantity, l_numerator, l_denominator, true);
          END IF;
        ELSE
          l_to_alt_uom_id := FetchAltUom(p_to_uom_cd, p_sap_material_id);
          IF (l_to_alt_uom_id IS NOT NULL) THEN
            -- Std => Alt
            FindAltOrBaseUomWithCommonDim( p_sap_material_id, l_base_uom_cd, p_from_uom_cd, l_alt_uom_cd, l_alt_uom_id);
            IF (l_alt_uom_id IS NULL) THEN
              -- Dimension => Base
              ConvertBetweenUomAndDimension( l_base_uom_cd, r_quantity, l_numerator, l_denominator, false);
            ELSE
              -- Dimension => Other Alt
              ConvertBetweenUomAndDimension( l_alt_uom_cd, r_quantity, l_numerator, l_denominator, false);
              -- Alt => Base
              ConvertBetweenAltUomAndBaseUom( l_alt_uom_id, r_quantity, l_numerator, l_denominator, true);
            END IF;
            -- Base => Alt
            ConvertBetweenAltUomAndBaseUom( l_to_alt_uom_id, r_quantity, l_numerator, l_denominator, false);
          ELSE
            -- Std => Std
            l_from_uom_dim_cd := GetDimensionCd(p_from_uom_cd);
            l_to_uom_dim_cd   := GetDimensionCd(p_to_uom_cd);

            IF (l_from_uom_dim_cd <> l_to_uom_dim_cd) THEN
              RAISE conv_error;
            END IF;

            -- Dimension => Std
            ConvertBetweenUomAndDimension( p_to_uom_cd, r_quantity, l_numerator, l_denominator, false);
          END IF;
        END IF;
      END IF;
    END IF;

    RETURN r_quantity;
  END;

END logon_uom;
/

SHOW ERRORS;

