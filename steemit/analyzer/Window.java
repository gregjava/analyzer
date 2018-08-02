package steemit.analyzer;


/**
 *
 * @author GregJava
 */
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import static javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;
import static javax.swing.JFrame.setDefaultLookAndFeelDecorated;
import javax.swing.UIManager;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;


/**
 * Defines a web application that can load pages from the tags section of the Steemit website.  
 * Almost all the functionality is provided automatically by the JEditorPane class.  
 * The program loads web pages asynchronously in a thread. This class will be run as a standalone application.
 */
public class Window extends Application {
    double FONTSIZE = 1.00, ZOOMSIZE = 1.00; WebView webView; Stage stage; BorderPane displayPanel; Button steemitTags;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { EventQueue.invokeLater(() -> { Application.launch(args); }); }
    
    /**
     *
     * @param stage The current value of font or zoom.
     * 
     * Starts the application window.
     */
    @Override
    public void start(final Stage stage) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        setDefaultLookAndFeelDecorated(true);
        Application.setUserAgentStylesheet(getClass().getResource("res/style/style.css").toExternalForm());
        stage.setTitle("Analyzer"); stage.setIconified(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("res/images/steemit_analyzer_logo.png")));
        setWebView(webView = new WebView(), FONTSIZE, ZOOMSIZE);
        extractTagAnalysis(stage, webView.getEngine(), steemitTags = new Button("Steemit tags"), "http://www.steemit.com/tags", new TextArea());
        VBox leftPane = createLeftPane(webView, displayPanel = new BorderPane(), steemitTags);
        leftPane.setAlignment(Pos.CENTER); // Try to centalize the Menu Pane
        setDisplayPanel(displayPanel, leftPane, webView, "-fx-padding: 5;-fx-border-style: solid inside;-fx-border-width: 2;-fx-border-insets: 5;-fx-border-radius: 5;-fx-border-color: blue;");
        createMenubar(displayPanel, stage);
        showWindow(stage, displayPanel, Color.ALICEBLUE);
        this.stage = stage; // Save local reference for stage
    }
    
    /**
     *
     * @param stage The application frame.
     * @param displayPanel The main display panel.
     * @param APPCOLOR The colour for the main application.
     * 
     * Set the main view to the desired application background colour and display the new view.
     */
    private void showWindow(Stage stage, BorderPane displayPanel, Color APPCOLOR) {
        stage.setScene(new Scene(displayPanel, Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2, APPCOLOR));
        stage.show(); // Display the Stage and keep track of interface
    }
    
    /**
     *
     * @param i The current value of font or zoom.
     * 
     * Reduce the font or zoom size by one unit (0.01).
     */
    private static double reduceFontZoom(double i){ return i-0.01; /* Reduce the font or zoom by 1% */ }
    
    /**
     *
     * @param i The current value of font or zoom.
     * 
     * Increase the font or zoom size by one unit (0.01).
     */
    private static double increaseFontZoom(double i){ return i+0.01; /* Increase the font or zoom by 1% */ }
    
    /**
     *
     * @param thisChar The character to be tested.
     * @return The boolean values 'true', if the character is a number, or else it returns 'false'.
     */
    final boolean charIsNum(char thisChar) {
        return thisChar=='0'||thisChar=='1'||thisChar=='2'||thisChar=='3'||thisChar=='4'||thisChar=='5'||thisChar=='6'||thisChar=='7'||thisChar=='8'||thisChar=='9';
    }
    
    /**
     *
     * @param webView The view on which the buttons take effect.
     * @param displayPanel The panel on which output will be displayed.
     * @param steemitTags The button for displaying the tag analysis.
     * @return A horizontal box with buttons as toolbar.
     */
    public VBox createLeftPane(WebView webView, BorderPane displayPanel, final Button steemitTags) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 0, 5, 0));
        vbox.setSpacing(2);
        vbox.getStyleClass().removeAll("paneStyle, focus"); 
        vbox.getStyleClass().add("paneStyle");
        Button analysis = new Button("         Analysis");
        styleTopicButton(analysis);
        
        setBtnDisabled(steemitTags);
        steemitTags.setOnAction((ActionEvent event) -> { setBtnDisabled(steemitTags);
            HBox analysisPanel = showOutputScreen(stage, displayPanel, new TextArea());
            analysisPanel.setAlignment(Pos.CENTER); displayPanel.setCenter(analysisPanel); });
        Button steem = new Button("Steem");
        styleButton(steem);
        steem.setOnAction((ActionEvent event) -> {  setBtnDisabled(steem);
            extractTagAnalysis(stage, webView.getEngine(), steem, "http://blocktivity.info", new TextArea());
            HBox analysisPanel = showOutputScreen(stage, displayPanel, new TextArea());
            analysisPanel.setAlignment(Pos.CENTER); displayPanel.setCenter(analysisPanel); });
        Button sbd = new Button("Steem Dollars");
        styleButton(sbd);
        sbd.setOnAction((ActionEvent event) -> {  });
        Button btc = new Button("Bitcoin");
        styleButton(btc);
        btc.setOnAction((ActionEvent event) -> {  });
        Button eth = new Button("Ethereum");
        styleButton(eth);
        eth.setOnAction((ActionEvent event) -> {  });
        Button otherAltcoins = new Button("More altcoins");
        styleButton(otherAltcoins);
        otherAltcoins.setOnAction((ActionEvent event) -> {  });
        Button chat = new Button("           Chat");
        styleTopicButton(chat);
        Button rooms = new Button("Rooms");
        styleButton(rooms);
        rooms.setOnAction((ActionEvent event) -> {  });
        Button contactList = new Button("Contact List");
        styleButton(contactList);
        contactList.setOnAction((ActionEvent event) -> {  });
        Button market = new Button("         Market");
        styleTopicButton(market);
        Button forex = new Button("Forex");
        styleButton(forex);
        forex.setOnAction((ActionEvent event) -> {  });
        Button commodities = new Button("Commodities");
        styleButton(commodities);
        commodities.setOnAction((ActionEvent event) -> {  });
        Button goodsServices = new Button("Goods & Services");
        styleButton(goodsServices);
        goodsServices.setOnAction((ActionEvent event) -> {  });
        Button account = new Button("        Account");
        styleTopicButton(account);
        Button deposit = new Button("Deposit");
        styleButton(deposit);
        deposit.setOnAction((ActionEvent event) -> {  });
        Button payNow = new Button("Pay Now");
        styleButton(payNow);
        payNow.setOnAction((ActionEvent event) -> {  });
        Button escrow = new Button("Escrow");
        styleButton(escrow);
        escrow.setOnAction((ActionEvent event) -> {  });
        Button[] separator = new Button[3];
        for (int i=0; i<separator.length; i++) {
            separator[i] = new Button("");
            separator[i].setPrefSize(170, 15);
            separator[i].setDisable(true);
            separator[i].setBackground(Background.EMPTY);
        }
        vbox.getChildren().addAll(analysis, steemitTags, steem, sbd, btc, eth, otherAltcoins, separator[0], 
                chat, rooms, contactList, separator[1], market, forex, commodities, 
                goodsServices, separator[2], account, deposit, payNow, escrow);

        return vbox;
    }
    
    /**
     *
     * @param infoScreen The area for analysis to be displayed.
     * @param displayPanel The main panel on whom the infoScreen will be set.
     * @param stage The main stage on whom the displayPanel is displayed.
     * @return A horizontal box with a text area for analysis display.
     */
    public HBox showOutputScreen(Stage stage, BorderPane displayPanel, TextArea infoScreen) {
        HBox hbox = new HBox();
        hbox.prefWidthProperty().bind(stage.widthProperty());
        hbox.setPadding(new Insets(1, 1, 1, 1));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");
        infoScreen.setPrefSize(displayPanel.getWidth(), displayPanel.getHeight());
        infoScreen.setEditable(false);
        hbox.getChildren().addAll(infoScreen);

        return hbox;
    }
    
    private void createMenubar(BorderPane mainPanel, Stage stage){
        MenuBar menuBar = new MenuBar();
        menuBar.setPadding(new Insets(10, 0, 5, 15));
        menuBar.getStyleClass().removeAll("paneStyle, focus"); 
        menuBar.getStyleClass().add("paneStyle");
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        mainPanel.setTop(menuBar);

        Menu generalMenu = new Menu("Menu");
        Menu fontMenu = new Menu("Font");
        styleMenu(fontMenu);
        ToggleGroup fontSizeToggleGroup = new ToggleGroup();
        RadioMenuItem fontIncInc = new RadioMenuItem("Font++");
        styleDropdown(fontIncInc, fontSizeToggleGroup);
        fontIncInc.setOnAction((ActionEvent event) -> { webView.setFontScale(FONTSIZE = increaseFontZoom(FONTSIZE+=0.1)); });
        fontIncInc.setSelected(true);
        RadioMenuItem fontInc = new RadioMenuItem("Font+");
        styleDropdown(fontInc, fontSizeToggleGroup);
        fontInc.setOnAction((ActionEvent event) -> { webView.setFontScale(FONTSIZE = increaseFontZoom(FONTSIZE)); });
        RadioMenuItem fontDec = new RadioMenuItem("Font-");
        styleDropdown(fontDec, fontSizeToggleGroup);
        fontDec.setOnAction((ActionEvent event) -> { webView.setFontScale(FONTSIZE = reduceFontZoom(FONTSIZE)); });
        RadioMenuItem fontDecDec = new RadioMenuItem("Font--");
        styleDropdown(fontDecDec, fontSizeToggleGroup);
        fontDecDec.setOnAction((ActionEvent event) -> { webView.setFontScale(FONTSIZE = reduceFontZoom(FONTSIZE-=0.1)); });
        fontMenu.getItems().addAll(fontIncInc,fontInc,fontDec,fontDecDec);

        Menu zoomMenu = new Menu("Zoom");
        styleMenu(zoomMenu);
        ToggleGroup zoomToggleGroup = new ToggleGroup();
        RadioMenuItem zoomIncInc = new RadioMenuItem("Zoom++");
        styleDropdown(zoomIncInc, zoomToggleGroup);
        zoomIncInc.setOnAction((ActionEvent event) -> { webView.setZoom(ZOOMSIZE = increaseFontZoom(ZOOMSIZE+=0.1)); });
        zoomIncInc.setSelected(true);
        RadioMenuItem zoomInc = new RadioMenuItem("Zoom+");
        styleDropdown(zoomInc, zoomToggleGroup);
        zoomInc.setOnAction((ActionEvent event) -> { webView.setZoom(ZOOMSIZE = increaseFontZoom(ZOOMSIZE)); });
        RadioMenuItem zoomDec = new RadioMenuItem("Zoom-");
        styleDropdown(zoomDec, zoomToggleGroup);
        zoomDec.setOnAction((ActionEvent event) -> { webView.setZoom(ZOOMSIZE = reduceFontZoom(ZOOMSIZE)); });
        RadioMenuItem zoomDecDec = new RadioMenuItem("Zoom--");
        styleDropdown(zoomDecDec, zoomToggleGroup);
        zoomDecDec.setOnAction((ActionEvent event) -> { webView.setZoom(ZOOMSIZE = reduceFontZoom(ZOOMSIZE-=0.1)); });
        zoomMenu.getItems().addAll(zoomIncInc,zoomInc,zoomDec,zoomDecDec);

        Menu themeMenu = new Menu("Theme");
        styleMenu(themeMenu);
        ToggleGroup themeToggleGroup = new ToggleGroup();
        RadioMenuItem theme1 = new RadioMenuItem("Theme 1");
        styleDropdown(theme1, themeToggleGroup);
        theme1.setOnAction((ActionEvent event) -> { webView.setStyle("-fx-background-color: #000000;-fx-text-color: #008080;"); });
        theme1.setSelected(true);
        RadioMenuItem theme2 = new RadioMenuItem("Theme 2");
        styleDropdown(theme2, themeToggleGroup);
        theme2.setOnAction((ActionEvent event) -> { webView.setStyle("-fx-background-color: #008080;-fx-text-color: #000000;"); });
        RadioMenuItem theme3 = new RadioMenuItem("Theme 3");
        styleDropdown(theme3, themeToggleGroup);
        theme3.setOnAction((ActionEvent event) -> { webView.setStyle("-fx-background-color: #336699;-fx-text-color: #ffffff;"); });
        RadioMenuItem theme4 = new RadioMenuItem("Theme 4");
        styleDropdown(theme4, themeToggleGroup);
        theme4.setOnAction((ActionEvent event) -> { webView.setStyle("-fx-background-color: #ffffff;-fx-text-color: #336699;"); });
        themeMenu.getItems().addAll(theme1,theme2,theme3,theme4);
        
        Menu bookmarkMenu = new Menu("Bookmarks");
        styleMenu(bookmarkMenu);
        
        ToggleGroup bookmarkToggleGroup = new ToggleGroup();
        RadioMenuItem addBookmark = new RadioMenuItem("Add bookmark");
        styleDropdown(addBookmark, bookmarkToggleGroup);
        addBookmark.setOnAction(actionEvent -> {});
        
        RadioMenuItem manageBookmark = new RadioMenuItem("Manage bookmarks");
        styleDropdown(manageBookmark, bookmarkToggleGroup);
        manageBookmark.setOnAction(actionEvent -> {});
        bookmarkMenu.getItems().addAll(addBookmark, manageBookmark);
        
        Menu navigationMenu = new Menu("Navigation");
        styleMenu(navigationMenu);
        
        ToggleGroup navigationToggleGroup = new ToggleGroup();
        RadioMenuItem lastMenuItem = new RadioMenuItem("Back <-");
        styleDropdown(lastMenuItem, navigationToggleGroup);
        lastMenuItem.setOnAction(actionEvent -> {});
        
        RadioMenuItem nextMenuItem = new RadioMenuItem("Next ->");
        styleDropdown(nextMenuItem, navigationToggleGroup);
        nextMenuItem.setOnAction(actionEvent -> {});
        navigationMenu.getItems().addAll(lastMenuItem,nextMenuItem);
        
        MenuItem exitMenuItem = new MenuItem("Exit");
        styleMenu(exitMenuItem);
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        generalMenu.getItems().addAll(fontMenu, new SeparatorMenuItem(), zoomMenu, new SeparatorMenuItem(), themeMenu, 
                new SeparatorMenuItem(), bookmarkMenu, new SeparatorMenuItem(), navigationMenu, new SeparatorMenuItem(), exitMenuItem);

        menuBar.getMenus().addAll(generalMenu);
    }

    private void styleMenu(MenuItem menuItem) {
        menuItem.getStyleClass().removeAll("level1Style, focus"); 
        menuItem.getStyleClass().add("level1Style");
    }

    private void styleButton(Button btn) {
        btn.getStyleClass().removeAll("btnStyle, focus"); // remove any previous style from button, except btn
        btn.getStyleClass().add("btnStyle"); // add the style from stylesheet
        btn.setPadding(new Insets(0,5,0,5));
        btn.setPrefSize(110, 15);
    }

    private void styleDropdown(RadioMenuItem menuItem, ToggleGroup toggleGroup) {
        menuItem.getStyleClass().removeAll("level2Style, focus"); 
        menuItem.getStyleClass().add("level2Style");
        if (toggleGroup!=null) menuItem.setToggleGroup(toggleGroup);
    }

    private void styleTopicButton(Button btn) {
        btn.setDisable(true); btn.setBackground(Background.EMPTY);
        btn.setPadding(new Insets(0,5,0,5));
        btn.setPrefSize(110, 15);
    }

    private void extractTagAnalysis(final Stage stage, final WebEngine webEngine, Button button, String site, final TextArea infoScreen) {
        // Update the stage title when a new web page title is available
        webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends State> ov, State oldState, State newState) -> {
            if (newState == State.SUCCEEDED) {
                try { TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
                    StringWriter stringWriter = new StringWriter(); Document doc = webEngine.getDocument(); 
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                    transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
                    String pageContent = stringWriter.getBuffer().toString();
                    if (site.equalsIgnoreCase("http://www.steemit.com/tags")) {
                        String temp = "", tmp = "", data = "\nTag\t\t\t\t\tPosts\t\t\t\t\tComments\t\t\t\tPayouts\t\t\t\t\tPayout/Post\n";
                        data = readTags(pageContent, temp, tmp, data); setBtnEnabled(button); 
                        final TagAnalyzer engine = TagAnalyzer.analyzer = new TagAnalyzer(); final String finalData = data; 
                        engine.clearAndSave(finalData, "input.saf", "UTF-8"); if (!new File("output.saf").exists()) System.exit(0); stage.setTitle(webEngine.getTitle());
                        try { infoScreen.setText(engine.readFile(Paths.get(new File("output.saf").getPath()),"UTF-8")); } catch(Exception e){infoScreen.setText(e.getMessage());}
                    } else if (site.equalsIgnoreCase("http://blocktivity.info")) {
                        setBtnEnabled(button); if (!new File("output.saf").exists()) System.exit(0); stage.setTitle(webEngine.getTitle());
                        try { infoScreen.setText(pageContent); } catch(Exception e){infoScreen.setText(e.getMessage());}
                    }
                } catch (IllegalArgumentException | TransformerException ex) { System.err.println(ex); }
            } // else extractAnalysis(stage, webEngine, button, site, infoScreen);
        }); webEngine.load(site); // Load the page
    }

    private void setWebView(WebView webView, double FONTSIZE, double ZOOMSIZE) {
        webView.setContextMenuEnabled(false); // Disable the context menu
        webView.setMaxHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        webView.setMaxWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        webView.setMinHeight(0.00); webView.setMinWidth(0.00);
        webView.setFontScale(FONTSIZE); webView.setZoom(ZOOMSIZE); // Set the Zoom and Font Size to 100%
        webView.setFontSmoothingType(FontSmoothingType.GRAY); // Set font smoothing type to GRAY
    }

    private void setDisplayPanel(BorderPane displayPanel, VBox menuPanel, WebView webView, String style) {
        displayPanel.setLeft(menuPanel); displayPanel.setCenter(webView); displayPanel.setStyle(style);
    }

    private void setBtnDisabled(Button btn) { btn.setDisable(true); }
    
    private void setBtnEnabled(Button btn) { btn.setDisable(false); styleButton(btn); }

    private String readTags(String pageContent, String temp, String tmp, String data) {
        if (pageContent.length()>8) for (int i=0; i<pageContent.length(); i++) {
            while(!temp.contains("<TBODY>")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++;}
            temp = ""; do { while (!temp.contains("href")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = ""; while (!temp.contains(">")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = ""; while (!temp.contains("<")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = temp.substring(0, temp.length()-1); if (temp.length()<5) data += temp+"\t\t\t\t\t";
            else if (temp.length()<10) data += temp+"\t\t\t\t"; else if (temp.length()<15) data += temp+"\t\t\t";
            else if (temp.length()<20) data += temp+"\t\t"; else data += temp+"\t";
            while (!temp.contains("<TD>")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = ""; while (!temp.contains("<")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = temp.substring(0, temp.length()-1); tmp = ""; for (int j=0; j<temp.length(); j++) if (charIsNum(temp.charAt(j))) tmp+=temp.charAt(j);
            double numOfPosts = Integer.parseInt(tmp); if (temp.length()<5) data += temp+"\t\t\t\t\t";
            else if (temp.length()<10) data += temp+"\t\t\t\t"; else if (temp.length()<15) data += temp+"\t\t\t";
            else if (temp.length()<20) data += temp+"\t\t"; else data += temp+"\t";
            while (!temp.contains("<TD>")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = ""; while (!temp.contains("<")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = temp.substring(0, temp.length()-1); if (temp.length()<5) data += temp+"\t\t\t\t\t";
            else if (temp.length()<10) data += temp+"\t\t\t\t"; else if (temp.length()<15) data += temp+"\t\t\t";
            else if (temp.length()<20) data += temp+"\t\t"; else data += temp+"\t";
            while (!temp.contains("<TD>")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = ""; while (!temp.contains("<")&&i<pageContent.length()) { temp += pageContent.charAt(i); i++; }
            temp = temp.substring(0, temp.length()-1); tmp = ""; for (int j=0; j<temp.length(); j++) if (charIsNum(temp.charAt(j))) tmp+=temp.charAt(j);
            double valOfPosts = Double.parseDouble(tmp); data += temp+"\t\t"+((valOfPosts/numOfPosts)/1000)+"\n";
            temp = ""; while (!temp.contains("</TBODY>")&&!temp.contains("href")&&i<pageContent.length()) { temp += pageContent.charAt(i);  i++; }
            } while (!temp.contains("</TBODY>")); if (temp.contains("</TBODY>")) break;
        } return data;
    }
}