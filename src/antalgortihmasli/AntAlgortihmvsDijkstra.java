package antalgortihmasli;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class AntAlgortihmvsDijkstra extends Application {
    private AnimationTimer animasyonZamanlayici;
   
    private Scene scene;
    private Stage window; 
    private Pane simulasyonpane;
          
    private List<Node> nodeListesi = new ArrayList();
    private List<Edge> kenarListesi = new ArrayList();
    private List<Ant> karincaListesi = new ArrayList();
    private List<Edge> eniyiRotaKenarListesi = new ArrayList();
    
    private int MAXNODES = 200;
    private final int MAXCONNECTIONS = 5;
    private int MAXANTS=100; 
    
    private final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    private final double SCREENWIDTH = screenBounds.getWidth();
    private final double SCREENHEIGHT = screenBounds.getHeight();
    private final double SIMULATIONMULTIPLIER = 0.8;
    
    private final double QSABITI = 5000;
    private double FEROMON = 1;
    
    private int duraksamaSayaci = 0;
    private final int duraksamaLimit = 20;
    
    private int nodeId = 0;
    private int kenarId = 0;
    private int karincaId = 0;
   
    private Node baslangicNode = null;
    private Node bitisNode = null;
    private double enKisaYol = Double.MAX_VALUE;
    private Button nodeUret;
    private Button kenarCiz; 
    private Button baslat;
    private Button reset;
    private Label lbAlpha,lbBeta,lbFeromonUcuculuk,lbKarinca;
    private TextArea log,simulasyonBilgisiKarinca,simulasyonBilgisiDijkstra;
    private ChoiceBox algorithmChoice;

    long simulationRuntime = 0;
    int simulationsRun = 0;
    private boolean simBasladi = false;
    private int maxIterations = 100;
    private long maxRuntime = 30;
    private Double alpha,beta,buharlasma;
   
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFXML.fxml"));
        Parent root = loader.load();
        scene = new Scene(root);
       
        simulasyonpane = (Pane)loader.getNamespace().get("simulasyonpane");
        nodeUret= (Button)loader.getNamespace().get("nodeUret");
        kenarCiz= (Button)loader.getNamespace().get("kenarCiz");
        baslat= (Button)loader.getNamespace().get("baslat");
        reset= (Button)loader.getNamespace().get("reset");
        log=(TextArea)loader.getNamespace().get("log");
        simulasyonBilgisiKarinca=(TextArea)loader.getNamespace().get("simulasyonBilgisiKarinca");
        simulasyonBilgisiDijkstra=(TextArea)loader.getNamespace().get("simulasyonBilgisiDijkstra");
        algorithmChoice=(ChoiceBox)loader.getNamespace().get("algorithmChoice");
        lbAlpha= (Label)loader.getNamespace().get("lbAlpha");
        lbBeta= (Label)loader.getNamespace().get("lbBeta");
        lbFeromonUcuculuk= (Label)loader.getNamespace().get("lbFeromonUcuculuk");
        lbKarinca= (Label)loader.getNamespace().get("lbKarinca");
        
        alpha= Double.valueOf(lbAlpha.getText());
        beta= Double.valueOf(lbBeta.getText());
        buharlasma=1-(Double.valueOf(lbFeromonUcuculuk.getText())/100);
        
        window=stage;
        window.setTitle("Karınca Algoritması & Dijkstra Algroritmasının Karşılaştırması");
        window.setFullScreenExitHint("Tam Ekrandan Çıkmak İçin ESC'ye Basınız...");
        window.setFullScreen(true);
        window.setResizable(true);
        
        ayarlaButtonHandler();
        ayarlaSceneEventHandler();
      
        animasyonZamanlayici = new AnimationTimer() {
            
            long karincaGuncelleZamanlayici = System.currentTimeMillis();
            long adimZamanlayici = System.currentTimeMillis();
            @Override
            public void handle(long now) {
                
                if (System.currentTimeMillis() - karincaGuncelleZamanlayici > 1) {
                    karincaGuncelle();
                    karincaGuncelleZamanlayici = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - adimZamanlayici > 1000) {
                    feremonGuncelle();
                    adimZamanlayici = System.currentTimeMillis();
                    runtimeGuncelle();
                    
                    if (!eniyiRotaKenarListesi.isEmpty()) {
                        simulasyonBilgisiKarinca.appendText(String.valueOf(simulationRuntime)+" , "+String.valueOf(Math.round(getYolUzunlugu(eniyiRotaKenarListesi)))+"\n");
                    }
                }
                 if (simBasladi) {
                    if (simulationsRun < maxIterations) {
                        if (simulationRuntime > maxRuntime) {
                            baslat.fire();
                            simulationsRun++;
                            reset.fire();
                        }
                    } else {
                        animasyonZamanlayici.stop();
                        reset.fire();
                        simulasyonDurdur();
                        log.appendText("Simülasyon sonlandı!\n");
                    }
                }
            
         } 
        };
         
        window.setScene(scene);
        window.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
    
    private void feremonGuncelle() {    
            buharlasma=1-(Double.valueOf(lbFeromonUcuculuk.getText())/100);
            
            if (!eniyiRotaKenarListesi.isEmpty()) {
                double rotaUzunlugu = getYolUzunlugu(eniyiRotaKenarListesi);

                for (Edge e : eniyiRotaKenarListesi) {
                    e.addFeromon((QSABITI/rotaUzunlugu) * FEROMON, false);
                }
               /* duraksamaSayaci++;
                if (duraksamaSayaci == duraksamaLimit) {
                    log.appendText( duraksamaLimit + " iterastondur aynı rota takip ediliyor, feromon güncellemesi yapıldı!\n");
                    for (Edge e : kenarListesi) {
                        if (!eniyiRotaKenarListesi.contains(e)) {
                            e.addFeromon(10, false);
                        }
                    }
                    duraksamaSayaci = 0;
                }*/
            }
            for (Edge ed : kenarListesi) {
                ed.setFeromon(ed.getFeromon()*buharlasma,true);
            }
        }
      
    private void ayarlaButtonHandler() {
        nodeUret.setOnAction(e -> {
            if (nodeListesi.size() != MAXNODES) {
                while (nodeListesi.size() < MAXNODES) {
                    rasgeleNodeEkle();
                }
                log.appendText("Rasgele Nodelar Üretildi!\n");
            } else {
                log.appendText(" Node limitine ulaşıldı!\n");
            }
        });
        
        kenarCiz.setOnAction(e -> { 
            Iterator<Node> nodeIter = nodeListesi.iterator();
            while (nodeIter.hasNext()) {
                Node element = nodeIter.next();
                List<Node> enYakinNodeListesi = new ArrayList<>(2);
                
                if (element.getBaglantiSayisi() != MAXCONNECTIONS) {
                    // EnYakın 3 node'a kenar çizilir
                    for (int i = 0; i < 3; i++) {
                        double enKisa = 9999;
                        for (Node n: nodeListesi) {
                            if (!element.equals(n)  && !enYakinNodeListesi.contains(n) && n.getBaglantiSayisi() != MAXCONNECTIONS) {
                                if (mesafeyiBul(element, n.getX(), n.getY()) < enKisa) {
                                    enKisa = mesafeyiBul(element, n.getX(), n.getY());
                                    if (enYakinNodeListesi.size() == i + 1) {
                                        enYakinNodeListesi.remove(i);
                                        enYakinNodeListesi.add(n);
                                    } else {
                                        enYakinNodeListesi.add(n);
                                    }
                                }
                            }
                        }
                    }
                    for (Node yakinNode: enYakinNodeListesi) {
                        kenarEkle(element, yakinNode, false);
                    }
                }
            }
           
            log.appendText("En Yakın Kenarlar Eklendi!\n");
            e.consume();
        });
        
        baslat.setOnAction(e -> { 
           if (getirBaslangicNode() != null && getirBitisNode() != null) {
                if (kenarListesi.isEmpty()) {
                   log.appendText(" Kenar Bulunmamaktadır!\n");
                } else if (baslangicNode.getBagliKenar().isEmpty() || bitisNode.getBagliKenar().isEmpty()) {
                    log.appendText(" Baslangıç ve Bitiş Nodu kenara sahip değil!\n");
                } else {
                    if (baslat.getText().equals("Çalıştır")) {
                        baslat.setText("Durdur");
                        log.appendText("Simulasyon Başlatıldı!\n");  
                       if(algorithmChoice.getSelectionModel().getSelectedItem().equals("Karınca Algoritması")){
                           
                           if (!eniyiRotaKenarListesi.isEmpty()) {
                                for (Edge edge : kenarListesi) {
                                    edge.setStroke(Color.BLACK);
                                    edge.setStrokeWidth(1.0);
                                }
                            }
                            eniyiRotaKenarListesi = new ArrayList();
                            while (karincaListesi.size() < Integer.valueOf(lbKarinca.getText())) {
                               karincaEkle();
                            }
                             animasyonZamanlayici.start();
                            }
                             else
                            {
                                 if (!eniyiRotaKenarListesi.isEmpty()) {
                                    for (Edge edge : kenarListesi) {
                                        edge.setStroke(Color.BLACK);
                                        edge.setStrokeWidth(1.0);
                                    }
                                 }
                                eniyiRotaKenarListesi = new ArrayList();
                                renkSil();
                                dijkstraEnKisaYoluBul(baslangicNode,bitisNode) ;
                                  if (baslat.getText().equalsIgnoreCase("Durdur")) { 
                                        baslat.setText("Çalıştır");
                                        animasyonZamanlayici.stop();
                                    }
                            }    
                    }
                    else {
                        baslat.setText("Çalıştır");
                        log.appendText("Simulasyon Durduruldu!\n");
                        animasyonZamanlayici.stop();
                    }
                }
            } else {
                log.appendText("Başlangıç ve  Bitiş Nodelarını Seçiniz!\n");
            }
            e.consume();
        });
        
        reset.setOnAction(e -> { 
            // Eğer çalışıyorsa simülasyonu durdur
            if (baslat.getText().equalsIgnoreCase("Durdur")) { 
                baslat.setText("Çalıştır");
                animasyonZamanlayici.stop();
            }
            
            simulasyonpane.getChildren().removeAll(karincaListesi);
            karincaListesi = new ArrayList();
            if (!eniyiRotaKenarListesi.isEmpty()) {
                for (Edge edge : kenarListesi) {
                    edge.setStroke(Color.BLACK);
                    edge.setStrokeWidth(1.0);
                }
            }
            for (Edge edge : kenarListesi) {
                edge.setFeromon(1.0, false);
            }
            eniyiRotaKenarListesi = new ArrayList();
            simulasyonBilgisiKarinca.appendText("----\n");
            
            karincaId = 0;
            enKisaYol = Double.MAX_VALUE;
            simulationRuntime = 0;
            log.clear();
            e.consume();
        });
        
    }
    
    private void ayarlaSceneEventHandler() {
        // Key events
        scene.setOnKeyReleased((KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                ke.consume();
                //window.close();
            } else if (ke.getCode() == KeyCode.ALT) {
                if (getirRenkliNode() != null) {
                    if (getirRenkliNode().baslangicMi()) {
                        getirRenkliNode().setBaslangic(false);
                        baslangicNode = null;
                        log.appendText("Node " + getirRenkliNode().getNodeID() + " başlangıç nodu silindi!\n");
                    } else if (getirRenkliNode().bitisMi()) {
                        log.appendText(" Node aynı zamanda bitiş nodu olarak seçildi!\n");
                    } else {
                        if (getirBaslangicNode() != null) {
                            getirBaslangicNode().setBaslangic(false);
                            baslangicNode = null;
                        }
                        getirRenkliNode().setBaslangic(true);
                        baslangicNode = getirRenkliNode();
                        log.appendText("Node " + getirRenkliNode().getNodeID() + " başlangıç nodu seçildi!\n");
                    }
                    
                    renkSil();
                }
                ke.consume();
            } else if (ke.getCode() == KeyCode.ALT_GRAPH) {
                if (getirRenkliNode() != null) {
                    if (getirRenkliNode().bitisMi()) {
                        getirRenkliNode().setBitis(false);
                        bitisNode = null;
                        log.appendText("Node " + getirRenkliNode().getNodeID() + " bitiş nodu silindi!\n");
                    } else if (getirRenkliNode().baslangicMi()) {
                        log.appendText(" Node aynı zamanda başlangıç nodu seçildi!\n");
                    } else {
                        if (getirBitisNode() != null) {
                            getirBitisNode().setBitis(false);
                            bitisNode = null;
                        }
                        getirRenkliNode().setBitis(true);
                        bitisNode = getirRenkliNode();
                        log.appendText("Node " + getirRenkliNode().getNodeID() + " bitiş nodu olarak seçildi!\n");
                    }
                    
                    renkSil();
                }
                ke.consume();
            } else if (ke.getCode() == KeyCode.H) {
                for (Ant a : karincaListesi) {
                    a.setVisible(!a.isVisible());
                }
            }
        });
        
        
        scene.setOnMousePressed((MouseEvent me) -> {
            
            boolean existingNodeSelected = false;
           if (me.isPrimaryButtonDown() && me.isControlDown()) {
                nodeSil(me);
                kenarSil(me);
                renkSil();
                
            } else if (me.isPrimaryButtonDown()) {
                Iterator<antalgortihmasli.Node> nodeIter = nodeListesi.iterator();
                while (nodeIter.hasNext()) {
                    antalgortihmasli.Node element = nodeIter.next();
                    if (element.equals(me.getTarget())) {
                        existingNodeSelected = true;
                        Node highlightedElement = getirRenkliNode();
                        if (!element.equals(highlightedElement)) {
                            if (getirRenkliNode() != null) {
                                kenarEkle(highlightedElement, element, true);
                                renkSil();
                            } else {
                                element.setRenkli(true);
                            }
                        } else {
                            renkSil();
                        }
                    }
                }
                if (!existingNodeSelected) {
                    if (getirRenkliNode() != null) {
                        renkSil();
                    } else {
                        nodeEkle(me);
                    }
                }                
            } 
            else if (me.isSecondaryButtonDown()) {
                rasgeleNodeEkle();
            }
        });
    }
    
    private void renkSil() {
        if (getirRenkliNode() != null) {
            getirRenkliNode().setRenkli(false);
        }
    }
    
    private Node getirRenkliNode() {
        Iterator<Node> nodeIter = nodeListesi.iterator();
        while (nodeIter.hasNext()) {
            Node element = nodeIter.next(); 
            if (element.renkliMi()) {
                return element;
            }
        }
        return null;
    }
    
    private Node getirBitisNode() {
        return bitisNode;
    }
    
    private void kenarEkle(Node baslangic, Node bitis, boolean mesaj) {
        if (baslangic.getBagliNodelar().contains(bitis)) {
            if (mesaj) {
                log.appendText(" Kenar mevcut!\n");
            }
        } else if (baslangic.getBaglantiSayisi() == MAXCONNECTIONS 
                || bitis.getBaglantiSayisi() == MAXCONNECTIONS) {
            if (mesaj) {
                log.appendText(" Maksimum bağlantı sayısına ulaşıldı!\n");
            }
        } else {
            Edge e = new Edge(kenarId, baslangic.getX(), baslangic.getY(), bitis.getX(), bitis.getY());
            kenarId++;

            simulasyonpane.getChildren().add(e);
            bitis.addBagliNode(baslangic);
            baslangic.addBagliKenar(e);
            bitis.addBagliKenar(e);
            e.addBagliNode(baslangic);
            e.addBagliNode(bitis);
            log.appendText(getYeniKenarYazdir(e));
            kenarListesi.add(e);
        } 
    }
    
    private Node getirBaslangicNode() {
        return baslangicNode;
    }
      
    private void nodeSil(MouseEvent me) {
        Iterator<Node> nodeIter = nodeListesi.iterator();
        while (nodeIter.hasNext()) {
            Node element = nodeIter.next(); 
            if (element.equals(me.getTarget())) {
                log.appendText("Node " + element.getNodeID() + " silindi!\n");
                Iterator<Edge> nodeKenarlari = element.getBagliKenar().iterator();
                while (nodeKenarlari.hasNext()) {
                    Edge elementEdge = nodeKenarlari.next();
                    log.appendText("Kenar " + elementEdge.getKenarId() + " silindi!\n");
                    for (Node n :elementEdge.getBagliNodelar()) {
                        if (!n.equals(element)) {
                            n.removeBagliNode(element);
                            n.removeBagliKenar(elementEdge);
                        }
                    }
                    simulasyonpane.getChildren().remove(elementEdge);
                    nodeKenarlari.remove(); 
                }
                simulasyonpane.getChildren().remove(element);
                nodeIter.remove(); 
            }
        }
    }
     
    private void kenarSil(MouseEvent me) {
        Iterator<Edge> kenarIterasyon = kenarListesi.iterator();
        while (kenarIterasyon.hasNext()) {
            Edge element = kenarIterasyon.next();
            if (element.equals(me.getTarget())) {
               log.appendText("Kenar " + element.getKenarId() + " silindi!\n");
                Node nodeOne = (Node) element.getBagliNodelar().get(0);
                Node nodeTwo = (Node) element.getBagliNodelar().get(1);

                nodeOne.removeBagliNode(nodeTwo);
                nodeOne.removeBagliKenar(element);
                nodeTwo.removeBagliNode(nodeOne);
                nodeTwo.removeBagliKenar(element);
                
                simulasyonpane.getChildren().remove(element);
                //en iyi rota bu kenarı içeriyorsa en iyi rotayı silmeliyiz
                if (eniyiRotaKenarListesi.contains(element)) {
                    for (Edge edge : kenarListesi) {
                        edge.setStroke(Color.BLACK);
                        edge.setStrokeWidth(1.0);
                    }
                    eniyiRotaKenarListesi = new ArrayList();
                }
                
                kenarIterasyon.remove(); 
            }
        }
    }
     
    private void nodeEkle(MouseEvent me) {
        // Simülasyon Bölgesinin içinde olmalı
        if (nodeListesi.size() < MAXNODES) {
            if (me.getX()-398>0 && me.getY() > 0) {
                Node n = new Node(nodeId, me.getX()-398, me.getY());
                nodeId++;
                simulasyonpane.getChildren().add(n);
                log.appendText(getYeniNodeYazdir(n));
                nodeListesi.add(n);
            } else {
                log.appendText(" Node'un yeri uygun değil!\n");
            }
        } else {
            log.appendText(" Node limitine ulaşıldı!\n");
        }
    }

    private void rasgeleNodeEkle() {
        if (nodeListesi.size() < MAXNODES) {
            double x, y;
            do {
                x = getRandomSayi((SCREENWIDTH * SIMULATIONMULTIPLIER));
                y = getRandomSayi(SCREENHEIGHT);
            } while (!uygunLokasyonMu(x, y));
            
            Node n = new Node(nodeId, Math.round(x), Math.round(y));
            nodeId++;
            simulasyonpane.getChildren().add(n);

            log.appendText(getYeniNodeYazdir(n));
            nodeListesi.add(n);
            
        } else {
            log.appendText(" Node limitine ulaşıldı!\n");
        }
        
        renkSil();
    }
   
    private Double getRandomSayi(double max) {
        Random r = new Random();
        return r.nextDouble() * max;    
    }
   
    private Boolean uygunLokasyonMu(double x, double y) {        
        Iterator<Node> nodeIter = nodeListesi.iterator();
        while (nodeIter.hasNext()) {
            Node element = nodeIter.next(); 
            if (mesafeyiBul(element, x, y) < 4.0) {
                return false;                
            }
        }
        return true;
    }
   
    private double mesafeyiBul(Node n, double x, double y) {
        return Math.sqrt(Math.pow(Math.abs((n.getX() - x)), 2) + Math.pow(Math.abs((n.getY() - y)), 2));
    }
   
    private void karincaEkle() {
        if (karincaListesi.size() < Integer.valueOf(lbKarinca.getText())) {
            Ant a = new Ant(karincaId, getirBaslangicNode());
            simulasyonpane.getChildren().add(a);
            karincaListesi.add(a);
            karincaId++;
        }
    }
     
    private void karincaGuncelle() {
        alpha= Double.valueOf(lbAlpha.getText());
        beta= Double.valueOf(lbBeta.getText());
        for (Ant a : karincaListesi) {
            if (a.gidismi()) {                
                if (a.getSonrakiNode() == null) {
                    a.kenarSec(alpha,beta);

                } else if (Math.abs(a.getCenterX() - a.getSonrakiNode().getCenterX()) < 5 && 
                        Math.abs(a.getCenterY() - a.getSonrakiNode().getCenterY()) < 5) {
                    a.setX(a.getSonrakiNode().getX() - 5.0);
                    a.setY(a.getSonrakiNode().getY() - 5.0);
                    Node aktifKontrolNode = a.getAktifNode();

                    a.setAktifNode(a.getSonrakiNode());
                    if (a.getAktifNode().equals(getirBitisNode())) { 
                        a.pushMevcutNodelar(getirBitisNode());
                        for (Edge e : getirBitisNode().getBagliKenar()) {
                            if (e.getBagliNodelar().contains(aktifKontrolNode)) {
                                a.pushMevcutKenarlar(e);
                            }
                        }
                        a.setOncekiRotaUzunlugu(yolUzunluguHesapla(a.getMevcutKenarlar()));
                        a.setSonrakiNode(null);
                        a.setGidis(false);
                    } else if (a.getAktifNode().getBagliNodelar().contains(getirBitisNode())) {
                        a.setSonrakiNode(getirBitisNode());
                        for (Edge e : getirBitisNode().getBagliKenar()) {
                            if (e.getBagliNodelar().contains(a.getAktifNode())) {
                                a.setAktifKenar(e);
                            }
                        }
                    } else {
                        if (a.getAktifNode().equals(getirBaslangicNode())) {
                            a.sifirla();
                        }
                        a.kenarSec(alpha,beta);
                    }

                } else {
                    a.gezmeyeBasla();
                }
            } else {
                if (a.getSonrakiNode() == null) {                               
                    a.setSonrakiNode(a.peekMevcutNodelar());
                    a.setAktifKenar(a.peekMevcutKenarlar());
                } else if (Math.abs(a.getCenterX() - a.getSonrakiNode().getCenterX()) < 5 && 
                        Math.abs(a.getCenterY() - a.getSonrakiNode().getCenterY()) < 5) {
                    a.setX(a.getSonrakiNode().getX() - 5.0);
                    a.setY(a.getSonrakiNode().getY() - 5.0);
                    a.setAktifNode(a.getSonrakiNode());
                    if (a.getAktifNode().equals(getirBaslangicNode())) {
                        a.setSonrakiNode(null);
                        a.setGidis(true);
                    } else if (a.getMevcutNodelar().size() == 0 && a.getMevcutKenarlar().size() == 0) {
                        a.setSonrakiNode(getirBaslangicNode());
                        for (Edge e : getirBaslangicNode().getBagliKenar()) {
                            if (e.getBagliNodelar().contains(a.getAktifNode())) {
                                a.setAktifKenar(e);
                            }
                        }
                    } else {
                        a.popMevcutNodelar();
                            a.setAktifKenar(a.peekMevcutKenarlar());
                            a.popMevcutKenarlar().addFeromon(QSABITI/a.getOncekiRotaUzunlugu() * FEROMON, false);
                        if (!a.getMevcutNodelar().empty()) {
                            a.setSonrakiNode(a.peekMevcutNodelar());
                        }
                    }
                } else {
                    a.gezmeyeBasla();
                }
            }
        }
    }
    
    private void dijkstraEnKisaYoluBul(Node baslangicNode,Node bitisNode) {
         long start = System.currentTimeMillis();
          Edge[] kenarlist=new  Edge[kenarListesi.size()];
                 int t=0,p=0;
                 for(Edge e:kenarListesi)
                 {    kenarlist[t]=e; t++;}
                  Node[] nodelist=new  Node[nodeListesi.size()];
                for(Node n:nodeListesi)
                { nodelist[p]=n; p++;}
                 
        Dijkstra dijkstra = new Dijkstra(nodelist,kenarlist, baslangicNode);
        List<Node> path = dijkstra.enKisaYoluOlustur(bitisNode);
        long toplamUzunluk = Math.round(dijkstra.getToplamUzaklik(bitisNode));
        long zaman=System.currentTimeMillis()-start;
        simulasyonBilgisiDijkstra.appendText(zaman+" , "+toplamUzunluk+"\n");
        List<Edge> enIyiRota=new ArrayList();
         for(Edge e:kenarListesi)
            {
                for (int i = 0; i < path.size(); i++) {
                if(i!=0)
                {  if(e.getBagliNodelar().get(0).getNodeID()== path.get(i).getNodeID()&&e.getBagliNodelar().get(1).getNodeID()== path.get(i-1).getNodeID())
                     {   enIyiRota.add(e);
                         break;
                     }
                      if(e.getBagliNodelar().get(1).getNodeID()== path.get(i).getNodeID()&&e.getBagliNodelar().get(0).getNodeID()== path.get(i-1).getNodeID())
                     {   enIyiRota.add(e);
                         break;
                     }
                }
                else
                     if(e.getBagliNodelar().get(0).getNodeID()== path.get(0).getNodeID()&&e.getBagliNodelar().get(1).getNodeID()== path.get(1).getNodeID())
                     {   enIyiRota.add(e);
                         break;
                     }
            }
        }
        enIyiRotayiCizveSakla(enIyiRota);
    }
   
    private Double yolUzunluguHesapla(List<Edge> routeEdges) {
        double geciciYolUzunlugu = getYolUzunlugu(routeEdges);
        
        String mes = "";
        for (Edge e : routeEdges) {
            mes += e.getKenarId() + ",";
        }
        
        if (eniyiRotaKenarListesi.isEmpty()) {
            enKisaYol = Math.round(geciciYolUzunlugu);
            enIyiRotayiCizveSakla(routeEdges);
            log.appendText("Bulunan en iyi rota: " + enKisaYol + "!\n");
            
        } else if (Math.round(geciciYolUzunlugu) < Math.round(getYolUzunlugu(eniyiRotaKenarListesi))) {
            enKisaYol = Math.round(geciciYolUzunlugu);
            enIyiRotayiCizveSakla(routeEdges);
            log.appendText("Bulunan en iyi rota: " + enKisaYol + "!\n");
            
        } else if (Math.round(geciciYolUzunlugu) == Math.round(getYolUzunlugu(eniyiRotaKenarListesi))) {
            log.appendText("En iyi yol Takip Ediliyor\n");
        }
        
        return geciciYolUzunlugu;
        
    }
  
    private double getYolUzunlugu(List<Edge> rota) {
        double yolUzunlugu = 0;
        for (Edge e : rota) {
            yolUzunlugu += e.getUzunluk();
        }
        return yolUzunlugu;
    }
   
    private void enIyiRotayiCizveSakla(List<Edge> enIyiKenarlar) {
        if (!eniyiRotaKenarListesi.isEmpty()) {
            for (Edge e : kenarListesi) {
                e.setStroke(Color.BLACK);
                e.setStrokeWidth(1.0);
            }
        }
        eniyiRotaKenarListesi = new ArrayList(enIyiKenarlar);
        for (Edge e : eniyiRotaKenarListesi) {
            e.setStroke(Color.BLUE);
            e.setStrokeWidth(2.0);
        }
    }
   
    private void runtimeGuncelle() {
        simulationRuntime += 1;
    }
       
    private void simulasyonDurdur() {
        simBasladi = false;
        simulationsRun = 0;
    }
     
    private String getYeniNodeYazdir(Node n) {
        StringBuilder sb = new StringBuilder("Node eklendi! (ID: " + n.getNodeID());
        sb.append(" x: ").append(n.getX());
        sb.append(" y: ").append(n.getY());
        sb.append(")\n");
        return sb.toString();
    }
      
    private String getYeniKenarYazdir(Edge e) {
        StringBuilder sb = new StringBuilder("Kenar Eklendi! (ID: " + e.getKenarId());
        sb.append(" (x: ").append(e.getStartX());
        sb.append(" y: ").append(e.getStartY());
        sb.append(") (x: ").append(e.getEndX());
        sb.append(" y: ").append(e.getEndY());
        sb.append("))\n");
        return sb.toString();
    }
   
}
