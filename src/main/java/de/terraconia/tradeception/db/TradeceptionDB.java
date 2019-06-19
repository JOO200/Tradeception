package de.terraconia.tradeception.db;

import de.baba43.api.plugin.CustomPlugin;
import de.baba43.lib.async.db.SQLController;
import de.baba43.lib.async.db.v2.FutureDbController;
import de.baba43.lib.log.IReporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TradeceptionDB extends FutureDbController {
    private static final String TRADECEPTION_DB = "survival_global.tradeception_log";

    public TradeceptionDB(CustomPlugin plugin, IReporter log, SQLController db) {
        super(plugin, log, db);
        createTables();
    }

    private void createTables() {
        try(Connection conn = getConnection()) {
            var stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + TRADECEPTION_DB + " (" +
                    "id INT(11) NOT NULL AUTO_INCREMENT, " +
                    "player VARCHAR(36) NOT NULL, " +
                    "time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "amount INT(11) NOT NULL, " +
                    "first INT(11) NOT NULL, " +
                    "second INT(11) NOT NULL, " +
                    "result INT(11) NOT NULL, " +
                    "primary key(id))");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> insertTrades(List<TradeModel> models) {
        return async(conn -> {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO " + TRADECEPTION_DB + " (player, amount, first, second, result) VALUES (?,?,?,?,?);");
            for (TradeModel model : models) {
                model.toStmt(stmt);
                stmt.addBatch();
                stmt.clearParameters();
            }
            stmt.executeBatch();
            return null;
        });
    }
}
