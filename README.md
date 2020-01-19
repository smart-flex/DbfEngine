# DbfEngine Java API

DbfEngine - a Java API to read, write xBase(DBASE, Foxpro dbf files). Also API allows read memory files (.mem) of Foxpro.
Samples for both operations you may see at [DbfEngine javadoc](https://www.smart-flex.ru/htm/de_api/index.html)

This API is pure lightweight library without memory consumption and any third party libraries (there are no java loggers and etc.)
The DBF Java API is intended as engine for data exchange purposes

## Features

Engine is very small and fast.
API for reading is made as iterator, API for writing is made in manner as JDBC statement. It is allows to write compact Java code.

Also you can look inside your dbf file by invoking it through command line:
java -jar dbfEngine_bin-1.10.jar your.dbf

The result of parsing dbf header and content will be placed into textual file.

## Limitations

This version was tested under MS Foxpro 2.6 without memo field support.

## Requirements

DbfEngine requires JDK 1.6 or higher.

## Licensing

DbfEngine is issued on under the GNU Lesser General Public License.

