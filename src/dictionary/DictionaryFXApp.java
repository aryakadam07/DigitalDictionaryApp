package dictionary;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class DictionaryFXApp extends Application {

    // ── Design tokens (Obsidian Curator palette) ──────────────────────────────
    private static final String BG              = "#131313";
    private static final String SURFACE_LOW     = "#1b1b1b";
    private static final String SURFACE         = "#1f1f1f";
    private static final String SURFACE_HIGH    = "#2a2a2a";
    private static final String SURFACE_HIGHEST = "#353535";
    private static final String SURFACE_LOWEST  = "#0e0e0e";
    private static final String PRIMARY         = "#adc6ff";
    private static final String PRIMARY_CONT    = "#4b8eff";
    private static final String TERTIARY        = "#e9c400";
    private static final String ON_SURFACE      = "#e2e2e2";
    private static final String ON_SURF_VAR     = "#c1c6d7";
    private static final String ON_SURF_MUTED   = "#8b90a0";

    private final DictionaryService service = new DictionaryService();

    private final List<String> searchHistory = new ArrayList<>();
    private final List<String> bookmarks     = new ArrayList<>();

    private TextField searchField;
    private VBox      suggestionsBox;
    private VBox      resultContainer;
    private VBox      historyList;
    private Label     statusLabel;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");
        root.setLeft(buildSidebar());
        root.setTop(buildTopNav());

        ScrollPane scrollPane = new ScrollPane(buildMainContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1100, 720);
        stage.setTitle("The Lexicon | Digital Dictionary");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();

        showPlaceholderResult();
    }

    // ── TOP NAV ──────────────────────────────────────────────────────────────
    private HBox buildTopNav() {
        HBox nav = new HBox();
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(14, 24, 14, 24));
        nav.setSpacing(32);
        nav.setStyle(
            "-fx-background-color: rgba(19,19,19,0.90);" +
            "-fx-border-color: transparent transparent rgba(173,198,255,0.05) transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label logo = lbl("The Lexicon", "Manrope", 20, FontWeight.BLACK, PRIMARY);
        Label explore    = navLink("Explore", true);
        Label booksLnk   = navLink("Bookmarks", false);
        booksLnk.setOnMouseClicked(e -> showBookmarks());
        Label histLnk    = navLink("History", false);
        histLnk.setOnMouseClicked(e -> showHistoryPane());

        HBox links = new HBox(24, explore, booksLnk, histLnk);
        links.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label gear = lbl("\u2699", "System", 18, FontWeight.NORMAL, ON_SURF_VAR);
        gear.setStyle(gear.getStyle() + "-fx-cursor: hand;");

        nav.getChildren().addAll(logo, links, spacer, gear);
        return nav;
    }

    private Label navLink(String text, boolean active) {
        Label l = new Label(text.toUpperCase());
        l.setFont(Font.font("Manrope", FontWeight.BOLD, 11));
        l.setStyle(
            "-fx-text-fill: " + (active ? PRIMARY : SURFACE_HIGHEST) + ";" +
            "-fx-cursor: hand;" +
            (active ? "-fx-border-color: transparent transparent " + PRIMARY + " transparent;" +
                      "-fx-border-width: 0 0 2 0; -fx-padding: 0 0 4 0;" : "")
        );
        return l;
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setStyle("-fx-background-color: " + SURFACE_LOWEST + ";");
        sidebar.setPadding(new Insets(80, 0, 24, 0));

        // User chip
        HBox userChip = new HBox(10);
        userChip.setAlignment(Pos.CENTER_LEFT);
        userChip.setPadding(new Insets(0, 20, 28, 20));
        Circle avatar = new Circle(20);
        avatar.setFill(Color.web(SURFACE_HIGHEST));
        Label avatarLbl = lbl("LC", "Manrope", 11, FontWeight.BLACK, PRIMARY);
        StackPane avatarStack = new StackPane(avatar, avatarLbl);
        VBox userInfo = new VBox(2,
            lbl("The Curator", "Manrope", 13, FontWeight.BLACK, ON_SURFACE),
            lbl("Lexical enthusiast", "Inter", 10, FontWeight.NORMAL, ON_SURF_MUTED)
        );
        userChip.getChildren().addAll(avatarStack, userInfo);

        VBox navItems = new VBox(2,
            sideNavItem("\uD83C\uDFE0", "Home", false),
            sideNavItem("\uD83D\uDD50", "Recent", true),
            sideNavItem("\uD83D\uDD16", "Favorites", false),
            sideNavItem("\u2B50", "Premium", false)
        );

        Label historyHeader = sectionHeader("Quick History");
        historyList = new VBox(10);
        historyList.setPadding(new Insets(0, 20, 0, 20));

        Label savedHeader = sectionHeader("Saved Lists");
        VBox savedLists = new VBox(10);
        savedLists.setPadding(new Insets(0, 20, 0, 20));
        savedLists.getChildren().addAll(
            savedListItem("Academic Prep"),
            savedListItem("Poetry Words")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button proBt = new Button("GO PRO");
        proBt.setMaxWidth(Double.MAX_VALUE);
        proBt.setStyle(
            "-fx-background-color: linear-gradient(to right, " + PRIMARY + ", " + PRIMARY_CONT + ");" +
            "-fx-text-fill: #001a41;" +
            "-fx-font-family: Manrope;" +
            "-fx-font-weight: 900;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 10 16;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        );
        HBox proBtWrap = new HBox(proBt);
        proBtWrap.setPadding(new Insets(0, 20, 0, 20));
        HBox.setHgrow(proBt, Priority.ALWAYS);

        sidebar.getChildren().addAll(
            userChip, navItems,
            historyHeader, historyList,
            savedHeader, savedLists,
            spacer, proBtWrap
        );
        return sidebar;
    }

    private HBox sideNavItem(String icon, String label, boolean active) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 20, 10, 20));
        item.setStyle(active
            ? "-fx-background-color: " + SURFACE + "; -fx-background-radius: 0 100 100 0; -fx-cursor: hand;"
            : "-fx-cursor: hand;");
        Label ico = lbl(icon, "System", 14, FontWeight.NORMAL, active ? PRIMARY : ON_SURF_MUTED);
        Label lbl = lbl(label, "Inter", 13, FontWeight.MEDIUM, active ? PRIMARY : ON_SURF_MUTED);
        item.getChildren().addAll(ico, lbl);
        return item;
    }

    private Label sectionHeader(String text) {
        Label h = lbl(text.toUpperCase(), "Inter", 9, FontWeight.BLACK, ON_SURF_MUTED);
        h.setPadding(new Insets(20, 20, 8, 20));
        return h;
    }

    private HBox savedListItem(String name) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(
            lbl("\uD83D\uDCC1", "System", 12, FontWeight.NORMAL, ON_SURF_MUTED),
            lbl(name, "Inter", 12, FontWeight.NORMAL, ON_SURF_VAR)
        );
        row.setStyle("-fx-cursor: hand;");
        return row;
    }

    // ── MAIN CONTENT ─────────────────────────────────────────────────────────
    private VBox buildMainContent() {
        VBox main = new VBox(32);
        main.setPadding(new Insets(40, 60, 60, 60));
        main.setStyle("-fx-background-color: " + BG + ";");

        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(
            lbl("Digital Dictionary", "Manrope", 40, FontWeight.BLACK, ON_SURFACE),
            lbl("Search words instantly — meanings, phonetics & suggestions",
                "Inter", 15, FontWeight.NORMAL, ON_SURF_MUTED)
        );

        VBox searchHero = buildSearchHero();

        HBox resultsGrid = new HBox(20);
        HBox.setHgrow(resultsGrid, Priority.ALWAYS);
        resultContainer = new VBox();
        HBox.setHgrow(resultContainer, Priority.ALWAYS);
        VBox sidePanel = buildSidePanel();
        sidePanel.setPrefWidth(300);
        sidePanel.setMinWidth(260);
        resultsGrid.getChildren().addAll(resultContainer, sidePanel);

        main.getChildren().addAll(header, searchHero, resultsGrid);
        return main;
    }

    // ── SEARCH HERO ──────────────────────────────────────────────────────────
    private VBox buildSearchHero() {
        VBox hero = new VBox(14);
        hero.setMaxWidth(720);
        hero.setAlignment(Pos.CENTER);

        HBox searchBar = new HBox(12);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(8, 8, 8, 20));
        searchBar.setStyle(
            "-fx-background-color: rgba(31,31,31,0.4);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(255,255,255,0.05);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;"
        );

        Label searchIcon = lbl("\uD83D\uDD0D", "System", 16, FontWeight.NORMAL, ON_SURF_VAR);

        searchField = new TextField();
        searchField.setPromptText("Enter a word...");
        searchField.setFont(Font.font("Inter", FontWeight.MEDIUM, 18));
        searchField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + ON_SURFACE + ";" +
            "-fx-prompt-text-fill: rgba(193,198,215,0.3);" +
            "-fx-border-color: transparent;"
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn = new Button("Search");
        searchBtn.setFont(Font.font("Manrope", FontWeight.BOLD, 14));
        searchBtn.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: #002e69;" +
            "-fx-padding: 12 28;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        );

        searchBar.getChildren().addAll(searchIcon, searchField, searchBtn);

        suggestionsBox = new VBox(8);
        suggestionsBox.setAlignment(Pos.CENTER);
        statusLabel = lbl("", "Inter", 12, FontWeight.NORMAL, ON_SURF_MUTED);

        Runnable doSearch = () -> performSearch(searchField.getText().trim().toLowerCase());
        searchBtn.setOnAction(e -> doSearch.run());
        searchField.setOnAction(e -> doSearch.run());
        searchField.textProperty().addListener((obs, o, n) -> {
            if (!n.isBlank()) showLiveSuggestions(n.trim().toLowerCase());
            else suggestionsBox.getChildren().clear();
        });

        hero.getChildren().addAll(searchBar, suggestionsBox, statusLabel);
        return hero;
    }

    // ── SIDE PANEL ───────────────────────────────────────────────────────────
    private VBox buildSidePanel() {
        VBox panel = new VBox(16);

        VBox etymCard = glassCard();
        etymCard.getChildren().addAll(
            cardHeader("\uD83D\uDCD6", "Etymology"),
            lbl("Search a word to see its origin and history.",
                "Inter", 13, FontWeight.NORMAL, ON_SURF_VAR)
        );

        VBox wotd = new VBox(6);
        wotd.setPadding(new Insets(20));
        wotd.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + SURFACE + ", " + SURFACE_LOWEST + ");" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(233,196,0,0.1);" +
            "-fx-border-radius: 20; -fx-border-width: 1;"
        );
        Label wotdWord = lbl("Serendipity", "Manrope", 22, FontWeight.BLACK, ON_SURFACE);
        Label wotdDef = lbl("The occurrence of events by chance in a happy way.",
                             "Inter", 12, FontWeight.NORMAL, ON_SURF_VAR);
        wotdDef.setWrapText(true);
        Button learnMore = new Button("Learn More \u2192");
        learnMore.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + TERTIARY + ";" +
            "-fx-font-family: Manrope; -fx-font-weight: 900; -fx-font-size: 11px;" +
            "-fx-cursor: hand; -fx-padding: 4 0;"
        );
        learnMore.setOnAction(e -> { searchField.setText("serendipity"); performSearch("serendipity"); });
        Label wotdTag = lbl("WORD OF THE DAY", "Inter", 9, FontWeight.BLACK, TERTIARY);
        wotd.getChildren().addAll(wotdTag, wotdWord, wotdDef, learnMore);

        VBox statsCard = glassCard();
        statsCard.getChildren().addAll(
            cardHeader("\uD83D\uDCCA", "Session Stats"),
            statRow("Words searched", String.valueOf(service.getFrequencyMap().size())),
            statRow("Bookmarked", String.valueOf(bookmarks.size()))
        );

        panel.getChildren().addAll(etymCard, wotd, statsCard);
        return panel;
    }

    // ── SEARCH LOGIC ─────────────────────────────────────────────────────────
    private void showLiveSuggestions(String prefix) {
        List<String> suggestions = service.prefixSuggestions(prefix);
        Platform.runLater(() -> {
            suggestionsBox.getChildren().clear();
            if (suggestions.isEmpty()) return;
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER);
            row.getChildren().add(lbl("Suggestions:", "Inter", 11, FontWeight.BOLD, ON_SURF_MUTED));
            for (String s : suggestions.subList(0, Math.min(5, suggestions.size()))) {
                Button chip = chipButton(s);
                chip.setOnAction(e -> { searchField.setText(s); performSearch(s); });
                row.getChildren().add(chip);
            }
            suggestionsBox.getChildren().add(row);
        });
    }

    private void performSearch(String word) {

    searchHistory.remove(word);
    searchHistory.add(0, word);

    if (searchHistory.size() > 8)
        searchHistory.remove(searchHistory.size() - 1);

    updateHistoryList();

    VBox loading = new VBox(
            lbl("Searching...", "Inter", 18, FontWeight.BOLD, ON_SURF_MUTED)
    );
    loading.setPadding(new Insets(40));
    resultContainer.getChildren().setAll(loading);

    new Thread(() -> {

        boolean found = service.searchWord(word);

        String meaning = service.getMeaning(word);

        Platform.runLater(() -> {

            // If API returned valid meaning
            if (!meaning.equals("Meaning not available for this word.")) {

                List<String> prefixes = service.prefixSuggestions(word);
                showDefinitionResult(word, meaning, prefixes);
                statusLabel.setText("");

            } else if (found) {

                List<String> prefixes = service.prefixSuggestions(word);
                showDefinitionResult(word, meaning, prefixes);

            } else {

                List<String> suggestions = service.autoCorrect(word);
                showNotFoundResult(word, suggestions);
                statusLabel.setText("Word not found.");
            }
        });

    }).start();
}

    private void showDefinitionResult(String word, String rawMeaning, List<String> prefixes) {
        String[] lines = rawMeaning.split("\n");
        String phonetic = "", type = "", definition = "";
        for (String line : lines) {
            if (line.startsWith("Phonetic: "))  phonetic   = line.substring(10);
            else if (line.startsWith("Type: "))  type       = line.substring(6);
            else if (line.startsWith("Meaning: "))definition = line.substring(9);
        }
        if (definition.isEmpty()) definition = rawMeaning;

        VBox card = glassCard();
        card.setPadding(new Insets(32));

        // Word row
        HBox wordRow = new HBox(16);
        wordRow.setAlignment(Pos.CENTER_LEFT);
        Label wordLabel = lbl(cap(word), "Manrope", 52, FontWeight.BLACK, ON_SURFACE);
        Button speakBtn = circleButton("\uD83D\uDD0A");
        speakBtn.setOnAction(e -> animateSpeaker(speakBtn));
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        Button bookmarkBtn = circleButton(bookmarks.contains(word) ? "\uD83D\uDD16" : "\u2606");
        bookmarkBtn.setOnAction(e -> {
            if (bookmarks.contains(word)) { bookmarks.remove(word); bookmarkBtn.setText("\u2606"); }
            else { bookmarks.add(word); bookmarkBtn.setText("\uD83D\uDD16"); }
        });
        wordRow.getChildren().addAll(wordLabel, speakBtn, sp2, bookmarkBtn);

        // Meta row
        HBox metaRow = new HBox(12);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        if (!type.isEmpty()) {
            Label typeBadge = new Label(type.toUpperCase());
            typeBadge.setFont(Font.font("Inter", FontWeight.BLACK, 10));
            typeBadge.setStyle(
                "-fx-background-color: rgba(233,196,0,0.1);" +
                "-fx-text-fill: " + TERTIARY + ";" +
                "-fx-background-radius: 6; -fx-padding: 3 8;"
            );
            metaRow.getChildren().add(typeBadge);
        }
        if (!phonetic.isEmpty() && !phonetic.equals("N/A")) {
            metaRow.getChildren().add(lbl(phonetic, "Inter", 13, FontWeight.NORMAL, ON_SURF_MUTED));
        }

        // Definition
        Label defLabel = lbl(definition, "Inter", 20, FontWeight.MEDIUM, ON_SURFACE);
        defLabel.setWrapText(true);

        // Related words
        List<String> acWords = service.autoCorrect(word);
        HBox extraGrid = new HBox(12);
        extraGrid.getChildren().addAll(
            wordInfoBox("Related Words",
                prefixes.isEmpty() ? List.of(word) : prefixes.subList(0, Math.min(4, prefixes.size())),
                PRIMARY, true),
            wordInfoBox("You Might Like",
                acWords.subList(0, Math.min(3, acWords.size())),
                ON_SURF_VAR, false)
        );

        card.getChildren().addAll(wordRow, metaRow, defLabel, extraGrid);
        resultContainer.getChildren().setAll(card);

        FadeTransition ft = new FadeTransition(Duration.millis(350), card);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private void showNotFoundResult(String word, List<String> suggestions) {
        VBox card = glassCard();
        Label title = lbl("\"" + word + "\" not found", "Manrope", 28, FontWeight.BLACK, ON_SURFACE);
        Label sub   = lbl("Not in the local dictionary. Did you mean:", "Inter", 14, FontWeight.NORMAL, ON_SURF_VAR);
        sub.setWrapText(true);
        HBox chips = new HBox(10);
        chips.setAlignment(Pos.CENTER_LEFT);
        for (String s : suggestions) {
            Button chip = chipButton(s);
            chip.setOnAction(e -> { searchField.setText(s); performSearch(s); });
            chips.getChildren().add(chip);
        }
        card.getChildren().addAll(title, sub, chips);
        resultContainer.getChildren().setAll(card);
        FadeTransition ft = new FadeTransition(Duration.millis(300), card);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private void showPlaceholderResult() {
        VBox ph = new VBox(12);
        ph.setAlignment(Pos.CENTER);
        ph.setPadding(new Insets(60));
        Label msg = lbl("Search for any English word above to get started",
                        "Inter", 16, FontWeight.NORMAL, ON_SURF_MUTED);
        msg.setWrapText(true); msg.setAlignment(Pos.CENTER);
        ph.getChildren().addAll(lbl("\uD83D\uDCDA", "System", 48, FontWeight.NORMAL, ON_SURF_MUTED), msg);
        resultContainer.getChildren().setAll(ph);
    }

    private void showHistoryPane() {
        VBox card = glassCard();
        card.getChildren().add(cardHeader("\uD83D\uDD50", "Search History"));
        if (searchHistory.isEmpty()) {
            card.getChildren().add(lbl("No history yet.", "Inter", 13, FontWeight.NORMAL, ON_SURF_MUTED));
        } else {
            FlowPane fp = new FlowPane(8, 8);
            for (String h : searchHistory) {
                Button b = chipButton(cap(h));
                b.setOnAction(e -> { searchField.setText(h); performSearch(h); });
                fp.getChildren().add(b);
            }
            card.getChildren().add(fp);
        }
        resultContainer.getChildren().setAll(card);
    }

    private void showBookmarks() {
        VBox card = glassCard();
        card.getChildren().add(cardHeader("\uD83D\uDD16", "Bookmarks"));
        if (bookmarks.isEmpty()) {
            card.getChildren().add(
                lbl("No bookmarks yet. Search a word and tap ☆ to bookmark it.",
                    "Inter", 13, FontWeight.NORMAL, ON_SURF_MUTED));
        } else {
            FlowPane fp = new FlowPane(8, 8);
            for (String b : bookmarks) {
                Button btn = chipButton(cap(b));
                btn.setStyle(btn.getStyle().replace(ON_SURF_VAR, PRIMARY));
                btn.setOnAction(e -> { searchField.setText(b); performSearch(b); });
                fp.getChildren().add(btn);
            }
            card.getChildren().add(fp);
        }
        resultContainer.getChildren().setAll(card);
    }

    // ── COMPONENT HELPERS ────────────────────────────────────────────────────
    private VBox glassCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: rgba(31,31,31,0.4);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(255,255,255,0.05);" +
            "-fx-border-radius: 20; -fx-border-width: 1;"
        );
        return card;
    }

    private HBox cardHeader(String icon, String title) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(
            lbl(icon, "System", 16, FontWeight.NORMAL, PRIMARY),
            lbl(title, "Manrope", 16, FontWeight.BOLD, ON_SURFACE)
        );
        return row;
    }

    private VBox wordInfoBox(String heading, List<String> words, String color, boolean highlight) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(14));
        box.setStyle(
            "-fx-background-color: rgba(42,42,42,0.4);" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(255,255,255,0.05);" +
            "-fx-border-radius: 14; -fx-border-width: 1;"
        );
        HBox.setHgrow(box, Priority.ALWAYS);
        FlowPane chips = new FlowPane(6, 6);
        for (String w : words) {
            Label chip = lbl(w, "Inter", 12, FontWeight.NORMAL, color);
            chip.setStyle(
                "-fx-background-color: " + (highlight ? "rgba(173,198,255,0.05)" : "transparent") + ";" +
                "-fx-background-radius: 4; -fx-padding: 2 6;" +
                "-fx-border-color: rgba(255,255,255,0.05); -fx-border-radius: 4; -fx-border-width: 1;"
            );
            chips.getChildren().add(chip);
        }
        box.getChildren().addAll(lbl(heading.toUpperCase(), "Inter", 9, FontWeight.BLACK, ON_SURF_MUTED), chips);
        return box;
    }

    private HBox statRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(
            lbl(label, "Inter", 12, FontWeight.NORMAL, ON_SURF_VAR),
            sp,
            lbl(value, "Manrope", 13, FontWeight.BLACK, PRIMARY)
        );
        return row;
    }

    private Button chipButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Inter", FontWeight.NORMAL, 13));
        btn.setStyle(
            "-fx-background-color: " + SURFACE_HIGH + ";" +
            "-fx-text-fill: " + ON_SURF_VAR + ";" +
            "-fx-background-radius: 100; -fx-padding: 5 14;" +
            "-fx-cursor: hand; -fx-border-color: transparent; -fx-border-radius: 100; -fx-border-width: 1;"
        );
        return btn;
    }

    private Button circleButton(String icon) {
        Button btn = new Button(icon);
        btn.setFont(Font.font("System", 16));
        btn.setStyle(
            "-fx-background-color: rgba(173,198,255,0.08);" +
            "-fx-background-radius: 100; -fx-padding: 8 10; -fx-cursor: hand;"
        );
        return btn;
    }

    private void animateSpeaker(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
        st.setFromX(1); st.setFromY(1);
        st.setToX(1.3); st.setToY(1.3);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private Label lbl(String text, String family, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font(family, weight, size));
        l.setStyle("-fx-text-fill: " + color + ";");
        return l;
    }

    private String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void updateHistoryList() {
        if (historyList == null) return;
        historyList.getChildren().clear();
        for (int i = 0; i < Math.min(4, searchHistory.size()); i++) {
            String h = searchHistory.get(i);
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            Label lbl = lbl(cap(h), "Inter", 12, FontWeight.NORMAL, ON_SURF_VAR);
            lbl.setStyle(lbl.getStyle() + "-fx-cursor: hand;");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            String ago = i == 0 ? "just now" : i + " ago";
            row.getChildren().addAll(lbl, sp, lbl(ago, "Inter", 10, FontWeight.NORMAL, ON_SURF_MUTED));
            final int idx = i;
            row.setOnMouseClicked(e -> { searchField.setText(searchHistory.get(idx)); performSearch(searchHistory.get(idx)); });
            historyList.getChildren().add(row);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
