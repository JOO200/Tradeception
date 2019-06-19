package de.terraconia.tradeception.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TradeModel {
    private int first, second, result;
    private UUID player;
    private int amount;

    public TradeModel(UUID player, int amount, int first, int second, int result) {
        this.player = player;
        this.amount = amount;
        this.first = first;
        this.second = second;
        this.result = result;
    }

    public TradeModel(ResultSet rs) throws SQLException {
        player = UUID.fromString(rs.getString("player"));
        amount = rs.getInt("amount");
        first = rs.getInt("first");
        second = rs.getInt("second");
        result = rs.getInt("result");
    }

    public void toStmt(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, player.toString());
        stmt.setInt(2, amount);
        stmt.setInt(3, first);
        stmt.setInt(4, second);
        stmt.setInt(5, result);
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public int getResult() {
        return result;
    }

    public UUID getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }
}
