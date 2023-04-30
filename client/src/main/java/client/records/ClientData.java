package client.records;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public final class ClientData implements Serializable {

    private static ClientData instance;
    private final HashMap<Long, Boolean> visitedBoards;

    /**
     * Creates a new ClientData object.
     */
    private ClientData() {
        this.visitedBoards = new HashMap<>();
    }

    /**
     * Returns the instance of ClientData.
     *
     * @return the instance of ClientData
     */
    public static ClientData getInstance() {
        if (instance == null) {
            try {
                readFromFile();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error while reading client data");
                instance = new ClientData();
            }
        }
        return instance;
    }

    /**
     * Saves the client data to a file.
     */
    public void saveToFile() {
        try {
            FileOutputStream fileOut = new FileOutputStream(
                    "client/src/main/java/client/records/clientData.ser"
            );
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(instance);
            out.close();
            fileOut.close();
            System.out.println("Client data saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the client data from a file.
     *
     * @throws IOException IOException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private static void readFromFile() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(
                "client/src/main/java/client/records/clientData.ser"
        );
        ObjectInputStream in = new ObjectInputStream(fileIn);
        instance = (ClientData) in.readObject();
        in.close();
        fileIn.close();
    }

    /**
     * Adds a board to the visited boards.
     *
     * @param boardId    board
     * @param isLocked isLocked
     */
    public void addVisitedBoard(long boardId, boolean isLocked) {
        visitedBoards.put(boardId, isLocked);
        saveToFile();
    }

    /**
     * Checks if the board has been visited.
     *
     * @param boardId board
     * @return true if the board has been visited
     */
    public boolean hasVisitedBoard(long boardId) {
        return visitedBoards.containsKey(boardId);
    }

    /**
     * Checks if the board has been unlocked by the user.
     *
     * @param boardId board
     * @return true if the board has been unlocked
     */
    public boolean hasUnlockedBoard(long boardId) {
        return visitedBoards.containsKey(boardId) &&
                !visitedBoards.get(boardId);
    }

    /**
     * @param o object
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientData that = (ClientData) o;
        return Objects.equals(visitedBoards, that.visitedBoards);
    }

    /**
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(visitedBoards);
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        return "ClientData{" +
                "visitedBoards=" + visitedBoards +
                '}';
    }
}
