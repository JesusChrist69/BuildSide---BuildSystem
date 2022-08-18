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

        public Select distinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }

        public Select columns(@NotNull String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        public Select where(@NotNull String... where) {
            this.where.addAll(Arrays.asList(where));
            return this;
        }

        public Select orderBy(@NotNull String... orderBy) {
            this.orderBy.addAll(Arrays.asList(orderBy));
            return this;
        }

        public Select groupBy(@NotNull String... groupBy) {
            this.groupBy.addAll(Arrays.asList(groupBy));
            return this;
        }

        public Select having(@NotNull String... having) {
            this.having.addAll(Arrays.asList(having));
            return this;
        }

        public Select join(@NotNull String... join) {
            this.join.addAll(Arrays.asList(join));
            return this;
        }

        public Select leftJoin(@NotNull String... leftJoin) {
            this.leftJoin.addAll(Arrays.asList(leftJoin));
            return this;
        }

        public Select rightJoin(@NotNull String... rightJoin) {
            this.rightJoin.addAll(Arrays.asList(rightJoin));
            return this;
        }

        public Select innerJoin(@NotNull String... innerJoin) {
            this.innerJoin.addAll(Arrays.asList(innerJoin));
            return this;
        }

        public Select outerJoin(@NotNull String... outerJoin) {
            this.outerJoin.addAll(Arrays.asList(outerJoin));
            return this;
        }

        public Select fullJoin(@NotNull String... fullJoin) {
            this.fullJoin.addAll(Arrays.asList(fullJoin));
            return this;
        }

        public Select crossJoin(@NotNull String... crossJoin) {
            this.crossJoin.addAll(Arrays.asList(crossJoin));
            return this;
        }

        public Select union(@NotNull String... union) {
            this.union.addAll(Arrays.asList(union));
            return this;
        }

        public Select unionAll(@NotNull String... unionAll) {
            this.unionAll.addAll(Arrays.asList(unionAll));
            return this;
        }

        public Select limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Select offset(int offset) {
            this.offset = offset;
            return this;
        }

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

        public Update columns(@NotNull String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        public Update values(@NotNull String... values) {
            this.values.addAll(Arrays.asList(values));
            return this;
        }

        public Update where(@NotNull String... where) {
            this.where.addAll(Arrays.asList(where));
            return this;
        }

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

        public Insert columns(@NotNull String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        public Insert values(@NotNull String... values) {
            this.values.addAll(Arrays.asList(values));
            return this;
        }

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

        public Delete where(@NotNull String... where) {
            this.where.addAll(Arrays.asList(where));
            return this;
        }

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

        public Create columnTypes(@NotNull String... types) {
            this.columnTypes.addAll(Arrays.asList(types));
            return this;
        }

        public Create columns(@NotNull String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        public Create primaryKey(@NotNull String primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        public Create ifNotExists() {
            this.ifNotExists = true;
            return this;
        }

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

        public Alter addColumns(@NotNull String... columns) {
            this.addColumn.addAll(Arrays.asList(columns));
            return this;
        }

        public Alter dropColumns(@NotNull String... columns) {
            this.dropColumn.addAll(Arrays.asList(columns));
            return this;
        }

        public Alter renameColumns(@NotNull String... columns) {
            this.renameColumn.addAll(Arrays.asList(columns));
            return this;
        }

        public Alter changeTypes(@NotNull String... columns) {
            this.changeType.addAll(Arrays.asList(columns));
            return this;
        }

        public Alter addPrimaryKey(@NotNull String primaryKey) {
            this.addPrimaryKey = primaryKey;
            return this;
        }

        public Alter dropPrimaryKey(boolean dropPrimaryKey) {
            this.dropPrimaryKey = dropPrimaryKey;
            return this;
        }

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

