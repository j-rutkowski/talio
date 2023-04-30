package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import commons.CardList;
import commons.Tag;
import commons.Task;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddCardCtrlTest {

    private AddCardCtrl sut;

    @Mock
    private ServerUtils server;

    @Mock
    private MainCtrl mainCtrl;

    TextField titleTextField;
    TextArea descriptionText;
    Text emptyTitle;
    CardList list;
    HBox tasks;
    HBox tagsBox;


    @BeforeAll
    static void initJfxRuntime() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        MockitoAnnotations.openMocks(this);
        sut = new AddCardCtrl(server, mainCtrl);
        titleTextField = new TextField("title");
        descriptionText = new TextArea("desc");
        emptyTitle = new Text("nothing");
        list = new CardList("Title", new ArrayList<>());
        tasks = new HBox();
        tagsBox = new HBox();
        sut.setEditedCardList(list);
        sut.setTitleTextField(titleTextField);
        sut.setDescriptionText(descriptionText);
        sut.setEmptyTitle(emptyTitle);
        sut.setTasks(tasks);
        sut.setTagsBox(tagsBox);
    }

//    @Test
//    public void initializeTest() {
//        tasks.getChildren().add(new HBox());
//        tagsBox.getChildren().add(new HBox());
//        assertEquals(sut.getDescriptionText().getText(), "desc");
//        assertEquals(sut.getTitleTextField().getText(), "title");
//
//        sut.add();
//
//        assertTrue(emptyTitle.isVisible());
//        assertEquals(sut.getDescriptionText().getText(), "");
//        assertEquals(sut.getTitleTextField().getText(), "");
//        assertEquals(sut.getTasks(), tasks);
//        assertEquals(sut.getTagsBox(), tagsBox);
//
//    }
//
//    @Test
//    public void editTest(){
//        List<Task> tasks = new ArrayList<>();
//        tasks.add(new Task("Task 1"));
//        tasks.add(new Task("Task 2"));
//        List<Tag> tags = new ArrayList<>();
//        tags.add(new Tag("Tag 1", "0xcacacaff"));
//        tags.add(new Tag("Tag 2", "0x00cacaff"));
//        tags.add(new Tag("Tag 3", "0x0000caff"));
//        Card card = new Card("Title", "Desc", list, tasks, tags);
//
//        sut.editInit(card);
//
//        assertEquals(sut.getTitleTextField().getText(), "Title");
//        assertEquals(sut.getDescriptionText().getText(), "Desc");
//        assertEquals(sut.getTasks().getChildren().size(), 1);
//        VBox vBox = (VBox) sut.getTasks().getChildren().get(0);
//        assertEquals(vBox.getChildren().size(), 2);
//        assertEquals(sut.getTagsBox().getChildren().size(), 3);
//    }
}
