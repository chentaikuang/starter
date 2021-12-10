package com.xiaochen.starter.shard.util;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

/**
 * 表面提取工具
 */
@Slf4j
public class ParseSqlTableUtil {

    private static CCJSqlParserManager ccjSqlParserManager = new CCJSqlParserManager();

    public static List<String> getTableNames(String sql) throws Exception {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        Statement statement = ccjSqlParserManager.parse(new StringReader(sql));
        if (statement instanceof Select) {
            return tablesNamesFinder.getTableList((Select) statement);
        } else {
            log.warn("statement type -> {}", statement.getClass().getTypeName());
        }
        return Collections.emptyList();
    }
}
