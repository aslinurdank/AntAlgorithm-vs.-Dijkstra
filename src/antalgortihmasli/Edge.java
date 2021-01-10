package antalgortihmasli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Line;

public class Edge extends Line implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    
    private int kenarId;
    private List<Node> bagliNodelar = new ArrayList<>(1); 
    private double feromon;
    private final double minFeromon = 1.0;
    private final double maxFeromon = 1000.0;
    private double yolUzunlugu;
    private double olasilik;
    
    public Edge(int id, double sourceX, double sourceY, double destX, double destY) {
        super(sourceX, sourceY, destX, destY);
        this.kenarId = id;
        this.yolUzunlugu = getKenarUzunlugu();
        this.feromon = minFeromon;
    }

    public int getKenarId() {
        return kenarId;
    }
   
    public double getOlasilik() {
        return olasilik;
    }
    
    public void setOlasilik(double olasilik) {
        this.olasilik= olasilik;
    }

    public List<Node> getBagliNodelar() {
        return bagliNodelar;
    }

    public void addBagliNode(Node n) {
        bagliNodelar.add(n);
    }

    public double getFeromon() {
        return Math.round(feromon);
    }

    public void setFeromon(double feromonMik, boolean isLimited) {
        if (isLimited) {
            if (feromonMik > minFeromon && feromonMik < maxFeromon) {
                this.feromon = feromonMik;
            } else if (feromonMik > maxFeromon) {
                this.feromon = maxFeromon;
            } else if (feromonMik < minFeromon) {
                this.feromon = minFeromon;
            } 
        } else {
            if (feromonMik < minFeromon) {
                this.feromon = minFeromon;
            } else {
                this.feromon = feromonMik;
            }
        }
    }
    
    public void addFeromon(double feromonMik, boolean isLimited) {
        if (isLimited) {
            if (this.feromon + feromonMik < maxFeromon) {
                this.feromon += feromonMik;
            } else {
                System.out.println("max feromon miktarı Kenar:"+this.getKenarId());
                this.feromon = maxFeromon;
            }
        } else {
            this.feromon += feromonMik;
        }
    }

    public double getUzunluk() {
        return yolUzunlugu ;
    }    
   
    public  double getKenarUzunlugu() {
           
        double dx = Math.abs((super.getStartX() - super.getEndX()));
        double dy = Math.abs((super.getStartY() - super.getEndY()));

        return Math.sqrt((dx * dx) + (dy * dy));
    }
   }

