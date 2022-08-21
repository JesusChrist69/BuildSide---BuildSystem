package cz.jesuschrist69.buildsystem.mysql.builder;

import cz.jesuschrist69.buildsystem.exceptions.BuildSystemException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * SqlBuilder v1.4
 * @author JesusChrist69
 * @version 1.4
 *
 * **/
public class SqlBuilder {

    private SqlBuilder() {
        throw new BuildSystemException("Cannot instantiate utility class.");
    }

    public static class Select {
        private boolean distinct = false;
        private final List<String> tables = new ArrayList<>();
        private final List<String> columns = new ArrayList<>();
        private final List<String> where = new ArrayList<>();
        private final List<String> orderBy = new ArrayList<>();
        private final List<String> groupBy = new ArrayList<>();
        private final List<String> having = new ArrayList<>();
        private final List<String> join = new ArrayList<>();
        private final List<String> leftJoin = new ArrayList<>();
        private final List<String> rightJoin = new ArrayList<>();
        private final List<String> innerJoin = new ArrayList<>();
        private final List<String> outerJoin = new ArrayList<>();
        private final List<String> fullJoin = new ArrayList<>();
        private final List<String> crossJoin = new ArrayList<>();
        private final List<String> union = new ArrayList<>();
        private final List<String> unionAll = new ArrayList<>();
        private int limit = 0;
        private int offset = 0;

        public Select(@NotNull String... tables) {
            this.tables.addAll(Arrays.asList(tables));
        }

        /**
         * > This function sets the distinct flag to true or false
         *
         * @param distinct Whether to select distinct rows.
         * @return The Select object itself.
         */
        public Select distinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }

        /**
         * > Adds the given columns to the list of columns to be selected
         *
         * @return The Select object itself.
         */
        public Select columns(@NotNull String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * > Adds the given strings to the list of where clauses
         *
         * @return The Select object itself.
         */
        public Select where(@NotNull String... where) {
            this.where.addAll(Arrays.asList(where));
            return this;
        }

        /**
         * This function adds the orderBy parameter to the orderBy list
         *
         * @return The Select object itself.
         */
        public Select orderBy(@NotNull String... orderBy) {
            this.orderBy.addAll(Arrays.asList(orderBy));
            return this;
        }

        /**
         * This function adds the given groupBy to the groupBy list
         *
         * @return The Select object itself.
         */
        public Select groupBy(@NotNull String... groupBy) {
            this.groupBy.addAll(Arrays.asList(groupBy));
            return this;
        }

        /**
         * > Adds the given having clauses to the query
         *
         * @return The Select object itself.
         */
        public Select having(@NotNull String... having) {
            this.having.addAll(Arrays.asList(having));
            return this;
        }

        /**
         * This function adds the given strings to the list of joins
         *
         * @return The Select object itself.
         */
        public Select join(@NotNull String... join) {
            this.join.addAll(Arrays.asList(join));
            return this;
        }

        /**
         * > Adds the given leftJoin to the list of leftJoins
         *
         * @return The Select object itself.
         */
        public Select leftJoin(@NotNull String... leftJoin) {
            this.leftJoin.addAll(Arrays.asList(leftJoin));
            return this;
        }

        /**
         * > Adds the given right join to the query
         *
         * @return The Select object itself.
         */
        public Select rightJoin(@NotNull String... rightJoin) {
            this.rightJoin.addAll(Arrays.asList(rightJoin));
            return this;
        }

        /**
         * > Adds the given inner join to the query
         *
         * @return The Select object itself.
         */
        public Select innerJoin(@NotNull String... innerJoin) {
            this.innerJoin.addAll(Arrays.asList(innerJoin));
            return this;
        }

        /**
         * > Adds the given outer join to the query
         *
         * @return The Select object itself.
         */
        public Select outerJoin(@NotNull String... outerJoin) {
            this.outerJoin.addAll(Arrays.asList(outerJoin));
            return this;
        }

        /**
         * > Adds the given fullJoin to the list of fullJoins
         *
         * @return The Select object itself.
         */
        public Select fullJoin(@NotNull String... fullJoin) {
            this.fullJoin.addAll(Arrays.asList(fullJoin));
            return this;
        }

        /**
         * > Adds the given crossJoin to the list of crossJoins
         *
         * @return The Select object itself.
         */
        public Select crossJoin(@NotNull String... crossJoin) {
            this.crossJoin.addAll(Arrays.asList(crossJoin));
            return this;
        }

        /**
         * > Adds the given union to the list of unions
         *
         * @return The Select object
         */
        public Select union(@NotNull String... union) {
            this.union.addAll(Arrays.asList(union));
            return this;
        }

        /**
         * > Adds a UNION ALL clause to the query
         *
         * @return The Select object itself.
         */
        public Select unionAll(@NotNull String... unionAll) {
            this.unionAll.addAll(Arrays.asList(unionAll));
            return this;
        }

        /**
         * > This function sets the limit of the number of rows to be returned by the query
         *
         * @param limit The number of rows to return.
         * @return The Select object is being returned.
         */
        public Select limit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * > Sets the offset of the first row to return
         *
         * @param offset The offset of the first row to return.
         * @return The Select object itself.
         */
        public Select offset(int offset) {
            this.offset = offset;
            return this;
        }

        /**
         * > This method builds a SQL query string from the various parts of the query
         *
         * @return A string
         */
        public String build() {
            assert !tables.isEmpty() : "No tables specified for select statement.";
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT ");
            if (distinct) {
                builder.append("DISTINCT ");
            }
            if (columns.isEmpty()) {
                builder.append("*");
            } else {
                builder.append(String.join(", ", columns));
            }
            builder.append(" FROM ");
            builder.append(String.join(", ", tables));
            if (!where.isEmpty()) {
                builder.append(" WHERE ");
                builder.append(String.join(" AND ", where));
            }
            if (!groupBy.isEmpty()) {
                builder.append(" GROUP BY ");
                builder.append(String.join(", ", groupBy));
            }
            if (!having.isEmpty()) {
                builder.append(" HAVING ");
                builder.append(String.join(" AND ", having));
            }
            if (!orderBy.isEmpty()) {
                builder.append(" ORDER BY ");
                builder.append(String.join(", ", orderBy));
            }
            if (limit > 0) {
                builder.append(" LIMIT ");
                builder.append(limit);
            }
            if (offset > 0) {
                builder.append(" OFFSET ");
                builder.append(offset);
            }
            if (!join.isEmpty()) {
                builder.append(" JOIN ");
                builder.append(String.join(", ", join));
            }
            if (!leftJoin.isEmpty()) {
                builder.append(" LEFT JOIN ");
                builder.append(String.join(", ", leftJoin));
            }
            if (!rightJoin.isEmpty()) {
                builder.append(" RIGHT JOIN ");
                builder.append(String.join(", ", rightJoin));
            }
            if (!innerJoin.isEmpty()) {
                builder.append(" INNER JOIN ");
                builder.append(String.join(", ", innerJoin));
            }
            if (!outerJoin.isEmpty()) {
                builder.append(" OUTER JOIN ");
                builder.append(String.join(", ", outerJoin));
            }
            if (!fullJoin.isEmpty()) {
                builder.append(" FULL JOIN ");
                builder.append(String.join(", ", fullJoin));
            }
            if (!crossJoin.isEmpty()) {
                builder.append(" CROSS JOIN ");
                builder.append(String.join(", ", crossJoin));
            }
            if (!union.isEmpty()) {
                builder.append(" UNION ");
                builder.append(String.join(", ", union));
            }
            if (!unionAll.isEmpty()) {
                builder.append(" UNION ALL ");
                builder.append(String.join(", ", unionAll));
            }
            return builder.toString();
        }
    }

    public static class Update {

        private final String table;
        private final List<String> columns = new ArrayList<>();
        private final List<String> values = new ArrayList<>();
        private final List<String> where = new ArrayList<>();

        public Update(@NotNull String table) {
            this.table = table;
        }

        /**
         * This function adds the columns to the list of columns to be updated
         *
         * @return The Update object itself.
         */
        public Update columns(@NotNull String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * This function takes a list of strings and adds them to the values list
         *
         * @return The Update object itself.
         */
        public Update values(@NotNull String... values) {
            this.values.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * > Adds the given strings to the list of where clauses
         *
         * @return The Update object
         */
        public Update where(@NotNull String... where) {
            this.where.addAll(Arrays.asList(where));
            return this;
        }

        /**
         * "If the number of columns is not equal to the number of values, throw an error, otherwise, build the query."
         *
         * The first thing we do is check if the number of columns is equal to the number of values. If it's not, we throw
         * an error
         *
         * @return A string
         */
        public String build() {
            assert columns.size() == values.size() : "Columns and values must have the same size! Columns: " + columns.size() + ", values: " + values.size();
            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE ");
            builder.append(table);
            builder.append(" SET ");
            for (int i = 0; i < columns.size(); i++) {
                builder.append("`");
                builder.append(columns.get(i));
                builder.append("` = '");
                builder.append(values.get(i));
                builder.append("'");
                if (i < columns.size() - 1) {
                    builder.append(", ");
                }
            }
            if (!where.isEmpty()) {
                builder.append(" WHERE ");
                builder.append(String.join(" AND ", where));
            }
            return builder.toString();
        }

    }

    public static class Insert {

        private final String table;
        private final List<String> columns = new ArrayList<>();
        private final List<String> values = new ArrayList<>();

        public Insert(@NotNull String table) {
            this.table = table;
        }

        /**
         * This function adds the columns to the list of columns
         *
         * @return The Insert object itself.
         */
        public Insert columns(@NotNull String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * This function takes a list of strings and adds them to the values list
         *
         * @return The Insert object itself.
         */
        public Insert values(@NotNull String... values) {
            this.values.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * "If the number of columns is not equal to the number of values, throw an error. Otherwise, build the query."
         *
         * The first line of the function is an assertion. Assertions are used to check if a condition is true. If the
         * condition is false, an error is thrown. In this case, the condition is that the number of columns is equal to
         * the number of values. If the condition is false, an error is thrown
         *
         * @return A string that is the SQL query.
         */
        public String build() {
            assert columns.size() == values.size() : "Columns and values must have the same size! Columns: " + columns.size() + ", values: " + values.size();
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO ");
            builder.append(table);
            builder.append(" (`");
            builder.append(String.join("`, `", columns));
            builder.append("`) VALUES ('");
            builder.append(String.join("', '", values));
            builder.append("')");
            return builder.toString();
        }

    }

    public static class Delete {

        private final String table;
        private final List<String> where = new ArrayList<>();

        public Delete(@NotNull String table) {
            this.table = table;
        }

        /**
         * > Adds a where clause to the query
         *
         * @return The Delete object itself.
         */
        public Delete where(@NotNull String... where) {
            this.where.addAll(Arrays.asList(where));
            return this;
        }

        /**
         * Builds a string that represents a SQL DELETE statement.
         *
         * @return A string that is a SQL query.
         */
        public String build() {
            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM ");
            builder.append(table);
            if (!where.isEmpty()) {
                builder.append(" WHERE ");
                builder.append(String.join(" AND ", where));
            }
            return builder.toString();
        }

    }

    public static class Create {

        private final String table;
        private final List<String> columns = new ArrayList<>();
        private final List<String> columnTypes = new ArrayList<>();
        private String primaryKey;

        private boolean ifNotExists = false;

        public Create(@NotNull String table) {
            this.table = table;
        }

        /**
         * This function adds the given types to the list of column types.
         *
         * @return The Create object itself.
         */
        public Create columnTypes(@NotNull String... types) {
            this.columnTypes.addAll(Arrays.asList(types));
            return this;
        }

        /**
         * This function adds the columns to the list of columns
         *
         * @return The Create object itself.
         */
        public Create columns(@NotNull String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * This function sets the primary key of the table
         *
         * @param primaryKey The name of the primary key column.
         * @return The Create object is being returned.
         */
        public Create primaryKey(@NotNull String primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        /**
         * If the table already exists, don't create it.
         *
         * @return The Create object
         */
        public Create ifNotExists() {
            this.ifNotExists = true;
            return this;
        }

        /**
         * If the number of columns is not equal to the number of column types, throw an error. Otherwise, create a table
         * with the given columns and types.
         *
         * @return A string that is a SQL statement to create a table.
         */
        public String build() {
            assert columns.size() == columnTypes.size() : "Columns and types must have the same size! Columns: " + columns.size() + ", types: " + columnTypes.size();
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE ");
            if (ifNotExists) {
                builder.append("IF NOT EXISTS ");
            }
            builder.append(table);
            builder.append(" (");
            for (int i = 0; i < columns.size(); i++) {
                builder.append(columns.get(i));
                builder.append(" ");
                builder.append(columnTypes.get(i));
                if (i < columns.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append(")");
            if (primaryKey != null) {
                builder.append(" PRIMARY KEY (");
                builder.append(primaryKey);
                builder.append(")");
            }
            return builder.toString();
        }
    }

    public static class Drop {

        private final String table;

        public Drop(@NotNull String table) {
            this.table = table;
        }

        /**
         * This function builds a string that drops a table if it exists.
         *
         * @return A string that will drop the table if it exists.
         */
        public String build() {
            StringBuilder builder = new StringBuilder();
            builder.append("DROP TABLE IF EXISTS ");
            builder.append(table);
            return builder.toString();
        }

    }

    public static class Truncate {

        private final String table;

        public Truncate(@NotNull String table) {
            this.table = table;
        }

        /**
         * Builds a string that represents a SQL statement to truncate a table.
         *
         * @return A string that is the SQL statement to truncate the table.
         */
        public String build() {
            StringBuilder builder = new StringBuilder();
            builder.append("TRUNCATE TABLE ");
            builder.append(table);
            return builder.toString();
        }

    }

    public static class Alter {

        private final String table;
        private final List<String> addColumn = new ArrayList<>();
        private final List<String> dropColumn = new ArrayList<>();
        private final List<String> renameColumn = new ArrayList<>();
        private final List<String> changeType = new ArrayList<>();

        private String addPrimaryKey;
        private boolean dropPrimaryKey = false;

        public Alter(@NotNull String table) {
            this.table = table;
        }

        /**
         * > Adds the given columns to the list of columns to be added
         *
         * @return The Alter object
         */
        public Alter addColumns(@NotNull String... columns) {
            this.addColumn.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * This function adds the columns to be dropped to the list of columns to be dropped
         *
         * @return The Alter object itself.
         */
        public Alter dropColumns(@NotNull String... columns) {
            this.dropColumn.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * > Adds the given columns to the list of columns to be renamed
         *
         * @return The Alter object
         */
        public Alter renameColumns(@NotNull String... columns) {
            this.renameColumn.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * This function adds the columns to the list of columns that will be changed
         *
         * @return The Alter object itself.
         */
        public Alter changeTypes(@NotNull String... columns) {
            this.changeType.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * > Adds a primary key to the table
         *
         * @param primaryKey The name of the primary key column.
         * @return The Alter object itself.
         */
        public Alter addPrimaryKey(@NotNull String primaryKey) {
            this.addPrimaryKey = primaryKey;
            return this;
        }

        /**
         * > Sets the flag to drop the primary key constraint
         *
         * @param dropPrimaryKey If true, the primary key will be dropped.
         * @return The Alter object itself.
         */
        public Alter dropPrimaryKey(boolean dropPrimaryKey) {
            this.dropPrimaryKey = dropPrimaryKey;
            return this;
        }

        /**
         * "If there are any changes to be made, build a string that contains the SQL command to make those changes."
         *
         * The function starts by creating a new StringBuilder object. This is a class that allows you to build a string by
         * appending other strings to it
         *
         * @return A string that is the SQL statement to alter the table.
         */
        public String build() {
            StringBuilder builder = new StringBuilder();
            builder.append("ALTER TABLE ");
            builder.append(table);
            if (!addColumn.isEmpty()) {
                builder.append(" ADD COLUMN ");
                builder.append(String.join(", ", addColumn));
            }
            if (!dropColumn.isEmpty()) {
                builder.append(" DROP COLUMN ");
                builder.append(String.join(", ", dropColumn));
            }
            if (!renameColumn.isEmpty()) {
                builder.append(" RENAME COLUMN ");
                builder.append(String.join(", ", renameColumn));
            }
            if (!changeType.isEmpty()) {
                builder.append(" CHANGE COLUMN ");
                builder.append(String.join(", ", changeType));
            }
            if (addPrimaryKey != null) {
                builder.append(" ADD PRIMARY KEY (");
                builder.append(addPrimaryKey);
                builder.append(")");
            }
            if (dropPrimaryKey) {
                builder.append(" DROP PRIMARY KEY");
            }
            return builder.toString();
        }

    }

}

