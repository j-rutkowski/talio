package server.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import server.database.BoardRepository;
import commons.Board;
import commons.CardList;
import server.services.RandomService;

//@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @MockBean
    private BoardRepository boardRepository = mock(BoardRepository.class);

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new BoardController(boardRepository, new RandomService())
        ).build();
    }

    @Test
    public void testAddBoard() throws Exception {
        Board board = new Board("test board", new ArrayList<>());
        when(boardRepository.save(board)).thenReturn(board);

        MockHttpServletRequestBuilder request = post("/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test board\"}");

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("{\"id\":0,\"cardLists\":[],\"tags\":[],\"title\":\"test board\",\"color\":null,");
        //verify(boardRepository).save(board);
    }
    @Test
    public void testGetAllBoards() throws Exception {
        List<Board> boards = new ArrayList<>();
        boards.add(new Board("board 1", new ArrayList<>()));
        boards.add(new Board("board 2", new ArrayList<>()));
        when(boardRepository.findAll()).thenReturn(boards);

        MockHttpServletRequestBuilder request = get("/boards")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("[{\"id\":0,\"cardLists\":[],\"tags\":[],\"title\":\"board 1\",\"color\":\"0xcacacaff\",");
        verify(boardRepository).findAll();
    }
    @Test
    public void testGetBoardById() throws Exception {
        Board board = new Board("test board", new ArrayList<>());
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        MockHttpServletRequestBuilder request = get("/boards/get-board")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("{\"id\":0,\"cardLists\":[],\"tags\":[],\"title\":\"test board\",\"color\":\"0xcacacaff\",");
        verify(boardRepository).findById(1L);
    }

    @Test
    public void testAddCardListToBoard() throws Exception {
        Board board = new Board("test board", new ArrayList<>());
        CardList cardList = new CardList("test list", new ArrayList<>());
        board.addCardList(cardList);
        when(boardRepository.save(board)).thenReturn(board);
        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.getById(1L)).thenReturn(board);

        MockHttpServletRequestBuilder request = post("/boards/add-list")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test list\"}");

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("{\"id\":0,\"cardLists\":[{\"id\":0,\"title\":\"test list\",\"cards\":[]},{\"id\":0,\"title\":\"test list\",\"cards\":null}],\"tags\":[],\"title\":\"test board\",\"color\":\"0xcacacaff\",");
        verify(boardRepository).getById(1L);
        verify(boardRepository).save(board);
    }

    @Test
    public void testDeleteBoard() throws Exception {
        when(boardRepository.existsById(1L)).thenReturn(true);

        MockHttpServletRequestBuilder request = delete("/boards/delete")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("");
        verify(boardRepository).deleteById(1L);
    }

    @Test
    public void testGetByKey(){
        Board board = new Board("title", new ArrayList<>());
        List<Board> boards = new ArrayList<>();
        board.setKey("ARandomKey");
        boards.add(board);
        String key = board.getKey();
        when(boardRepository.findAll()).thenReturn(boards);

        BoardController bc = new BoardController(boardRepository, null);

        assertThat(bc.getBoard(key).get()).isEqualTo(board);
        verify(boardRepository).findAll();
    }

    @Test
    public void renameBoardTest(){
        Board board = new Board("title", new ArrayList<>());
        Board board2 = new Board("New Title", new ArrayList<>());
        when(boardRepository.getById(1L)).thenReturn(board);
        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.save(board)).thenReturn(board);

        BoardController bc = new BoardController(boardRepository, null);

        assertThat(bc.renameBoard("New Title", 1)).isEqualTo(ResponseEntity.ok(board2));
        verify(boardRepository).getById(1L);
        verify(boardRepository).existsById(1L);
        verify(boardRepository).save(board);

    }

    @Test
    public void updateColor(){
        Board board = new Board("title", new ArrayList<>());
        Board board2 = new Board("title", new ArrayList<>());
        board2.setColor("color 1");
        when(boardRepository.getById(1L)).thenReturn(board);
        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.save(board)).thenReturn(board);

        BoardController bc = new BoardController(boardRepository, null);

        assertThat(bc.editTitle(1, "color 1")).isEqualTo(ResponseEntity.ok(board2));
        verify(boardRepository).getById(1L);
        verify(boardRepository).existsById(1L);
        verify(boardRepository).save(board);

    }


}




