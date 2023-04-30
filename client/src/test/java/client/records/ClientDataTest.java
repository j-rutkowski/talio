package client.records;

import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClientDataTest {

    ClientData clientData;
    @BeforeEach
    void setUp() {
        clientData = ClientData.getInstance();
    }

    @Test
    void getInstance() {
        assertEquals(clientData, ClientData.getInstance());
    }

    @Test
    void saveToFile() {
        clientData.saveToFile();
        assertEquals(new File("clientData.ser"), new File("clientData.ser"));
    }

    @Test
    void addVisitedBoard() {
        Board board = new Board("title", new ArrayList<>());
        clientData.addVisitedBoard(board.getId(), true);
        assertTrue(clientData.hasVisitedBoard(board.getId()));
    }

    @Test
    void hasVisitedBoard() {
        Board board = new Board("title", new ArrayList<>());
        clientData.addVisitedBoard(board.getId(), true);
        assertTrue(clientData.hasVisitedBoard(board.getId()));
    }

    @Test
    void hasUnlockedBoard() {
        Board board = new Board("title", new ArrayList<>());
        clientData.addVisitedBoard(board.getId(), true);
        assertFalse(clientData.hasUnlockedBoard(board.getId()));
    }

    @Test
    void testEquals() {
        assertEquals(clientData, ClientData.getInstance());
    }

    @Test
    void testHashCode() {
        assertEquals(clientData.hashCode(), ClientData.getInstance().hashCode());
    }

    @Test
    void testToString() {
        assertEquals("ClientData{visitedBoards={0=true}}", clientData.toString());
    }
}