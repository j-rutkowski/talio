package server.api;

import commons.Board;
import commons.Card;
import commons.CardList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.BoardRepository;
import server.database.CardRepository;
import server.database.ListRepository;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ListControllerTest {

    ListRepository listRepository;
    ArrayList<CardList> cardLists;
    ListController lc;
    CardList cardList;
    @BeforeEach
    public void setup(){
        Card card1 = new Card("title", "description");
        Card card2 = new Card("titles", "descriptions");
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(card1);
        cards.add(card2);
        cardList = new CardList("CARDLISTTITLE", cards);

        cardLists = new ArrayList<>();
        cardLists.add(cardList);

        listRepository = mock(ListRepository.class);
        lc = new ListController(listRepository, null, null, null);

    }
    @Test
    public void getAllLists() {

        Card card3 = new Card("title", "description");
        ArrayList<Card> cards2 = new ArrayList<Card>();
        cards2.add(card3);
        CardList cardList2 = new CardList("CARDLISTTITLE", cards2);

        cardLists.add(cardList2);

        when(listRepository.findAll()).thenReturn(cardLists);
        assertThat(lc.getAll()).isEqualTo(cardLists);
        verify(listRepository).findAll();
    }

    @Test
    public void gettingLists() {
        when(listRepository.findById(0l)).thenReturn(java.util.Optional.of(cardList));

        assertThat(lc.getList(0l).get()).isEqualTo(cardList);
        verify(listRepository).findById(0l);
    }



    @Test
    public void removingCard() {

        Card card3 = new Card("title", "description");
        ArrayList<Card> cards2 = new ArrayList<Card>();
        cards2.add(card3);
        CardList cardList2 = new CardList("CARDLISTTITLE", cards2);

        cardLists.add(cardList2);

        ListRepository listRepository = mock(ListRepository.class);
        BoardRepository boardRepository = mock(BoardRepository.class);

        when(listRepository.findAll()).thenReturn(cardLists);
        ListController lc = new ListController(listRepository, null, null, boardRepository);

        when(boardRepository.findById(0l)).thenReturn(java.util.Optional.of(new Board("TITLE", cardLists)));
        lc.removeCard(0l);
        verify(listRepository).save(cardList);
    }

    @Test
    public void EditCardBadRequest() {
        Card card1 = new Card("title", "description");

        CardController cardController = mock(CardController.class);
        CardRepository cardRepository = mock(CardRepository.class);
        BoardRepository boardRepository = mock(BoardRepository.class);

        lc = new ListController(listRepository, cardController, cardRepository, boardRepository);

        ResponseEntity<CardList> edited = lc.edit(-1L, card1);
        assertThat(edited).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    public void editCard() {

        Card card1 = new Card("title", "description");
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(card1);
        CardList cardList = new CardList("CARDLISTTITLE", cards);

        when(listRepository.getById(0l)).thenReturn(cardList);
        when(listRepository.existsById(0l)).thenReturn(true);
        lc.edit(0l, card1);
        verify(listRepository).existsById(0l);
        verify(listRepository).save(cardList);
    }

    @Test
    public void BadRequestAdd() {

        Card card1 = new Card("title", "description");
        Card card2 = new Card("titles", "descriptions");
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(card1);
        cards.add(card2);
        CardList cardList = new CardList("", cards);

        ListRepository listRepository = mock(ListRepository.class);
        ListController lc = new ListController(listRepository, null, null, null);

        ResponseEntity<CardList> ncl =  lc.add(cardList);
        assertThat(ncl).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    public void addCardList() {

        Card card1 = new Card("title", "description");
        Card card2 = new Card("titles", "descriptions");
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(card1);
        cards.add(card2);
        CardList cardList = new CardList("CARDLISTTITLE", cards);

        ArrayList<CardList> cardLists = new ArrayList<>();
        cardLists.add(cardList);

        ListRepository listRepository = mock(ListRepository.class);

        when(listRepository.findAll()).thenReturn(cardLists);
        ListController lc = new ListController(listRepository, null, null, null);
        when(listRepository.save(cardList)).thenReturn(cardList);
        ResponseEntity<CardList> ncl =  lc.add(cardList);
        assertThat(ncl).isEqualTo(ResponseEntity.ok(cardList));
    }

    @Test
    public void editNameTest(){
        when(listRepository.getById(0l)).thenReturn(cardList);
        when(listRepository.existsById(0l)).thenReturn(true);
        lc.editName(0L, "new Name");
        verify(listRepository).existsById(0l);
        verify(listRepository).save(cardList);
    }

    @Test
    public void reorderTest(){
        Card three = new Card("Title", "Desc");
        when(listRepository.getById(0l)).thenReturn(cardList);
        lc.editName(0L,2,three);
        assertThat(cardList.getCards().size()).isEqualTo(3);
        verify(listRepository).save(cardList);
    }

    @Test
    void removeList(){
        BoardRepository boardRepository = mock(BoardRepository.class);
        lc = new ListController(listRepository, null, null, boardRepository);
        Board b = new Board("board", cardLists);
        when(listRepository.getById(0L)).thenReturn(cardList);
        when(boardRepository.findById(0L)).thenReturn(Optional.of(b));
        when(boardRepository.save(b)).thenReturn(b);

        lc.removeList(0l, 0l);
        assertThat(b.getCardLists().size()).isEqualTo(0);
        verify(listRepository).getById(0L);
        verify(boardRepository).findById(0L);
        verify(boardRepository).save(b);

    }

    @Test
    void editTest(){
        Card three = new Card("Title", "Desc");
        when(listRepository.getById(0l)).thenReturn(cardList);
        when(listRepository.existsById(0l)).thenReturn(true);
        lc.edit(0L,three);
        assertThat(lc.edit(-1, three)).isEqualTo(ResponseEntity.badRequest().build());
        assertThat(cardList.getCards().size()).isEqualTo(3);
        verify(listRepository).save(cardList);
    }





}
