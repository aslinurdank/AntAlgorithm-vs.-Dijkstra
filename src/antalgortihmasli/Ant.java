package antalgortihmasli;

import java.util.Random;
import java.util.Stack;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ant extends Rectangle {    
    private final int antID;
    private final static double width = 10.0;
    private final static double height = 10.0;
    private Node aktifNode;
    private Edge aktifKenar;
    private Node sonrakiNode;
    private Edge oncekiKenar;
    private boolean gidis;
    private Stack<Edge> mevcutKenarlar;
    private Stack<Node> mevcutNodelar;
    
    private double oncekiRotaUzunlugu;
    public Ant(int antID, Node yuvaLokasyon) {
        super(yuvaLokasyon.getCenterX() - (width/2), yuvaLokasyon.getCenterY() - (height/2), width, height);
        super.setFill(Color.RED);
        this.antID = antID;
        this.aktifNode = yuvaLokasyon;
        this.gidis = true;
        this.mevcutKenarlar = new Stack<>();
        this.mevcutNodelar = new Stack<>();
    }
    
    public int getAntID() {
        return antID;
    }
  
    public double getCenterX() {
        return getX() + getWidth() / 2;
    }
    
    public double getCenterY() {
        return getY() + getHeight() / 2;
    }
  
    public Node getAktifNode() {
        return aktifNode;
    }

    public void setAktifNode(Node aktifNode) {
        this.aktifNode = aktifNode;
    }

    public void setAktifKenar(Edge aktifKenar) {
        this.aktifKenar = aktifKenar;
    }

    public Node getSonrakiNode() {
        return sonrakiNode;
    }

    public void setSonrakiNode(Node sonrakiNode) {
        this.sonrakiNode = sonrakiNode;
    }

    public boolean gidismi() {
        return gidis;
    }

    public void setGidis(boolean gidis) {
        this.gidis = gidis;
        if (!this.gidis) {
            super.setFill(Color.GREEN);
        } else {
            super.setFill(Color.RED);
        }
    }

    public Stack<Edge> getMevcutKenarlar() {
        return mevcutKenarlar;
    }

    public void pushMevcutKenarlar(Edge e) {
        this.mevcutKenarlar.push(e);   
    }
    
    public Edge popMevcutKenarlar() {
        return this.mevcutKenarlar.pop();
    }
    
    public Edge peekMevcutKenarlar() {
        return this.mevcutKenarlar.peek();
    }

    public Stack<Node> getMevcutNodelar() {
        return mevcutNodelar;
    }
    
    public void pushMevcutNodelar(Node n) {
        mevcutNodelar.push(n);
    }
    
    public Node popMevcutNodelar() {
        return this.mevcutNodelar.pop();
    }
    
    public Node peekMevcutNodelar() {
        return this.mevcutNodelar.peek();
    }

    public double getOncekiRotaUzunlugu() {
        return oncekiRotaUzunlugu;
    }

    public void setOncekiRotaUzunlugu(double oncekiRotaUzunlugu) {
        this.oncekiRotaUzunlugu = oncekiRotaUzunlugu;
    }
    
    public Edge kenarSec(double alpha,double beta) {
        int maxOlasilik=0;
        int maxNumGen = 0;
        if (aktifNode.getBagliKenar().size() == 2 && !aktifNode.baslangicMi()) {
            for (Edge e : aktifNode.getBagliKenar()) {
                if (!e.equals(oncekiKenar)) {

                    for (Node n : e.getBagliNodelar()) {
                        if (!n.equals(aktifNode)) {
                            sonrakiNode = n;
                            oncekiKenar = e;
                            Edge replacementEdge = dongudenCikar(n);
                            if (replacementEdge != null) {
                                e = replacementEdge;
                            }
                            
                            pushMevcutNodelar(n);
                            pushMevcutKenarlar(e);
                            aktifKenar = e;
                            return e;
                        }
                    }
                }
            }
            
        } else if (aktifNode.getBagliKenar().size() > 1) {
            for (Edge e : aktifNode.getBagliKenar()) {
                if (!e.equals(oncekiKenar)) {
                    e.setOlasilik(Math.pow( e.getFeromon(), alpha ) * Math.pow( 1/e.getUzunluk(), beta )<1?1:Math.pow( e.getFeromon(), alpha ) * Math.pow( 1/e.getUzunluk(), beta ));
                    maxOlasilik += e.getOlasilik();
                    }
            }
            for (Edge e : aktifNode.getBagliKenar()) {
                if (!e.equals(oncekiKenar)) {
                    e.setOlasilik(e.getOlasilik()/maxOlasilik<1?1:e.getOlasilik()/maxOlasilik);
                    maxNumGen += e.getOlasilik();
                    }
            }
            
            Random r = new Random();
            int selected = r.nextInt(maxNumGen+1);
            int toplam = 0;
            for (Edge e : aktifNode.getBagliKenar()) {
                    if (!e.equals(oncekiKenar)) {
                        toplam+= e.getOlasilik();
                        if (  toplam>=selected) {
                             for (Node n : e.getBagliNodelar()) {
                                if (!n.equals(aktifNode)) {
                                    sonrakiNode = n;
                                    oncekiKenar = e;
                                    Edge replacementEdge = dongudenCikar(n);
                                    if (replacementEdge != null) {
                                        e = replacementEdge;
                                    }
                                    pushMevcutNodelar(n);
                                    pushMevcutKenarlar(e);
                                    aktifKenar = e;
                                    return e;
                                }
                            }
                        }
                    }
            }
        } else {
            sonrakiNode = aktifNode.getBagliNodelar().get(0);
            dongudenCikar(sonrakiNode);
            pushMevcutNodelar(sonrakiNode);
            pushMevcutKenarlar(aktifNode.getBagliKenar().get(0));
            aktifKenar = aktifNode.getBagliKenar().get(0);
            oncekiKenar = aktifNode.getBagliKenar().get(0);
            return aktifNode.getBagliKenar().get(0);
        }
        return null;
    }
    
    public Edge dongudenCikar(Node eklenenNode) {
        while (mevcutNodelar.contains(eklenenNode)) {
            this.popMevcutNodelar().getNodeID();
            if (mevcutNodelar.contains(eklenenNode)) {
                this.popMevcutKenarlar().getKenarId(); 
            } else {
                return this.popMevcutKenarlar();
            }
        }
        return null;
    }
    
    public void gezmeyeBasla() {
        double adimPiksel = 5.0;
        
        double aktifNodeX = this.getAktifNode().getCenterX();
        double aktifNodeY = this.getAktifNode().getCenterY();
        double sonrakiNodeX = this.getSonrakiNode().getCenterX();
        double sonrakiNodeY = this.getSonrakiNode().getCenterY();
        double xFark = Math.abs(aktifNodeX - sonrakiNodeX);
        double yFark = Math.abs(aktifNodeY - sonrakiNodeY);
        double hipotenus = Math.sqrt(xFark * xFark + yFark * yFark);
        double xAdimBuyuklugu = ((xFark/hipotenus) * adimPiksel); 
        double yAdimBuyuklugu = ((yFark/hipotenus) * adimPiksel);

        if (aktifNodeX > sonrakiNodeX) {
            this.setX(this.getX() - xAdimBuyuklugu);
        } else if (aktifNodeX < sonrakiNodeX) {
            this.setX(this.getX() + xAdimBuyuklugu);
        }
        if (aktifNodeY > sonrakiNodeY) {
            this.setY(this.getY() - yAdimBuyuklugu);
        } else if (aktifNodeY < sonrakiNodeY) {
            this.setY(this.getY() + yAdimBuyuklugu);
        }
    }
    
    public void sifirla() {
        this.mevcutNodelar = new Stack<>();
        this.mevcutKenarlar = new Stack<>();
        this.oncekiKenar = null;
        this.sonrakiNode = null;
    }
}
