# Changelog
Version history

|Date      | Ver | Author                      | Description        |
|----------|-----|-----------------------------|--------------------|
|2019-07-10| 1.09| galisha.                    | Added handling for such bad numeric as: 1219,.0000 |
|2016-10-16| 1.08| galisha.                    | Added validation rules for dbf header in case handling of non-dbf files. |
|2016-07-11| 1.07| galisha.                    | Issued executable command line jar file. It's allows to parse dbf file from OOTB. Fixed error with reading unexpected zero in header of DBF and some code pages. **Sample of usage** : java -jar dbfEngine_bin-1.07.jar your.dbf |
|2015-03-01| 1.06| galisha.                    | Added validation method for dbf header. It's allow to check does this file is DBF file or not. Added method of existing a set of fields in dbf header. |
|2014-01-20| 1.05| galisha.                    | Added into DbfEngine functionality to get dbf header. Added reader for .mem file (new separate package ru.smartflex.tools.dbf.mem) |
|2013-08-26| 1.04| alexei.alexandrov@gmail.com.| Fix bug by reading DBF from zip-archive. (For case when stream is returning less data instead required). |
|2013-07-25| 1.03| galisha.                    | Enhanced exception messages (add current record info). Fix bug with empty value for numerical data. Suppress spaces for empty string. Found Eugene.|
|2013-05-25| 1.02| galisha.                    | Fix bug with reading 1 char in the field. Found Eugene.|
|2013-01-04| 1.01| galisha.                    | Added some code pages|
|2012-12-08| 1.00| galisha.                    | Initial version. Handles MS Foxpro 2.6 dbf file without memo support|
