/* NAME:       Jason Ou (ID: JAOU, ID#: 1385128) * DATE:       29 February 2016 * CLASS:      CMPS 182 - Winter 2016 * ASSIGNMENT: Lab Assignment 3 * FILES:      createview.sql queryview.sql unittests.sql createindex.sql foreign.sql general.sql README * SUBMIT:     submit cmps182-sf.w16 lab3 createview.sql queryview.sql unittests.sql createindex.sql foreign.sql general.sql README *//* Adds general constraints to the tables */ /* Price must be positive */ALTER TABLE h_medicines ADD CONSTRAINT positive_price CHECK (price > 0);/* An h_prescriptions record may not have a NULL value  * for the foreign key in doctor_id attribute  */ALTER TABLE h_prescriptions ALTER COLUMN doctor_id SET NOT NULL;/* An h_medicine record may not have a NULL name field. */ALTER TABLE h_medicines ALTER COLUMN name SET NOT NULL;