## Test task

### Implementation of some semblance of a data management language in a collection

### Main idea:

The task at hand is to implement a data management language in a collection. The basic commands that should be supported
are insertion of elements into the collection, deletion of an element from the collection, searching for elements in the
collection, and modifying elements in the collection. The collection structure is predefined.

The collection in this task is a structure that represents a table of data where each row in the table is an element of
the collection, and each column is a named attribute of the element.

The goal is to implement a method that takes a command as input in the form of a string (the format requirements will be
described below). The command should perform the four basic operations: insertion, modification, searching, and deletion
of elements in the data collection. In addition, when modifying, deleting, and searching, selection conditions from the
collection must be supported (they will be presented below).

The output should be a list of elements in the collection that were found, modified, added, or deleted.

### Requirements for the task:

The collection is a table represented by a `List<Map<String, Object>>`. Where List is a list of rows in the table, and
Map
is a mapping of the column name to its value for a particular row. Valid value types for the cells are `Long`, `Double`
, `Boolean`, and `String`.
