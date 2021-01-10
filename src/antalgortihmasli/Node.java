package antalgortihmasli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Node extends Circle implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    
    private int nodeID;
    private static final double SIZE = 4;
    private int MAXCONNECTIONS = 5;
    private boolean renkli = false; 
    private boolean baslangic = false; 
    private boolean bitis = false; 
    private List<Node> bagliNodelar = new ArrayList<>(MAXCONNECTIONS);
    private List<Edge> bagliKenarlar = new ArrayList<>(MAXCONNECTIONS);
    public Node previous;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Node(int id, double x, double y) {
        super(x, y, SIZE, Color.BLACK);
        this.nodeID = id;
    }

    public int getNodeID() {
        return nodeID;
    }

    public double getX() {
        return super.getCenterX();
    }

    public double getY() {
        return super.getCenterY();
    }

    public int getBaglantiSayisi() {
        return bagliNodelar.size();
    }

    public boolean renkliMi() {
        return renkli;
    }

    public void setRenkli(boolean renkli) {
        this.renkli = renkli;
        
        if (renkli) {
            super.setFill(Color.DARKMAGENTA);
        } else {
            if (this.baslangic) {
                super.setFill(Color.GREEN);
            } else if (this.bitis) {
                super.setFill(Color.RED);
            } else {
                super.setFill(Color.BLACK);    
            }
        }
    }

    public boolean baslangicMi() {
        return baslangic;
    }

    public void setBaslangic(boolean baslangic) {
        this.baslangic = baslangic;
        if (baslangic) {
            super.setFill(Color.GREEN);
            super.setRadius(SIZE*2);
        } else {
            super.setFill(Color.BLACK);
            super.setRadius(SIZE);
        }
    }

    public boolean bitisMi() {
        return bitis;
    }

    public void setBitis(boolean bitis) {
        this.bitis = bitis;

        if (bitis) {
            super.setFill(Color.RED);
            super.setRadius(SIZE*2);
        } else {
            super.setFill(Color.BLACK);
            super.setRadius(SIZE);
        }
    }

    public List<Node> getBagliNodelar() {
        return bagliNodelar;
    }
    
    public void addBagliNode(Node node) {
        if (bagliNodelar.size() != MAXCONNECTIONS) {
            if (!bagliNodelar.contains(node)) {
                // Mutually add the connection
                bagliNodelar.add(node);
                node.addBagliNode(this);
            }
        }
    }
    
    public void removeBagliNode(Node node) {
        if (bagliNodelar.size() > 0) {
            if (bagliNodelar.contains(node)) {
                bagliNodelar.remove(node);
                node.removeBagliNode(this);
            }
        }
    }

    public List<Edge> getBagliKenar() {
        return bagliKenarlar;
    }
    
    public void addBagliKenar(Edge e) {
        bagliKenarlar.add(e);
    }
    
    public void removeBagliKenar(Edge e) {
        bagliKenarlar.remove(e);
    }
  
    public double yolUzunluguHesapla(double xOther,double yOther){
        return (double) Math.sqrt(((this.getX()-xOther)*(this.getX()-xOther))+((this.getY()-yOther)*(this.getY()-yOther)));
    }
    
}
