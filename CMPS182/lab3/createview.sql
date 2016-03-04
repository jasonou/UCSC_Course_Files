/* NAME:       Jason Ou (ID: JAOU, ID#: 1385128) * DATE:       29 February 2016 * CLASS:      CMPS 182 - Winter 2016 * ASSIGNMENT: Lab Assignment 3 * FILES:      createview.sql queryview.sql unittests.sql createindex.sql foreign.sql general.sql README * SUBMIT:     submit cmps182-sf.w16 lab3 createview.sql queryview.sql unittests.sql createindex.sql foreign.sql general.sql README */ /* Working with views - creating a view *//* Creates a view named multiply_prescribed that contains the name/price of medicines * that have been prescribed at least twice, with the result sorted in ascending order * on the price of the medicines  */ CREATE VIEW multiply_prescribed AS SELECT m.name, m.price FROM h_medicines m, h_prescriptions pWHERE m.medicine_id = p.medicine_idGROUP BY m.name, m.priceHAVING COUNT(p.medicine_id) > 1ORDER BY m.price;/* Results in 15 rows */