package antalgortihmasli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Dijkstra {
    List<Node> nodelar = new ArrayList(); 
    List<Edge> kenarlar = new ArrayList(); 

    Node baslangicNodu;
    HashMap uzaklik = new HashMap(); 
    HashMap oncelik = new HashMap(); 
    HashMap oncelikKenar = new HashMap(); 
    List<Node> ziyaretEdilmemis = new ArrayList<>(); 

    public Dijkstra(Node newNodes[], Edge newEdges[], Node newStartNode){
        nodelar.addAll(Arrays.asList(newNodes));
        kenarlar.addAll(Arrays.asList(newEdges));
        baslangicNodu   = newStartNode;
        algoritmayiCalistir();
    }
  
    private void algoritmayiCalistir() {
        for (Node aktifNode : nodelar) {
            uzaklik.put(aktifNode.getNodeID(), Double.MAX_VALUE);
            oncelik.put(aktifNode.getNodeID(), null);
        }
        
        uzaklik.replace(baslangicNodu.getNodeID(), 0);
        ziyaretEdilmemis.addAll(nodelar);
        
        while (!ziyaretEdilmemis.isEmpty()) {
            Node aktifNode = getEnYakinNode();
            List<Node> komsular = getKomsular(aktifNode); 
            for (Node neighbourNode : komsular) {
                if(ziyaretEdilmemis.contains(neighbourNode)) {
                    uzaklikGuncelle(aktifNode,neighbourNode);
                }                        
            }
            if (aktifNode == null)
            {     
                  return;
            }
            ziyaretEdilmemis.remove(aktifNode);            
        }
    }
    
    private Node getEnYakinNode() {
        double mesafe = Double.MAX_VALUE;
        Node enYakinNode = null;

        for (Node aktifNode : ziyaretEdilmemis) {
            if ( Double.valueOf(uzaklik.get(aktifNode.getNodeID()).toString()) < mesafe) {
                mesafe =  Double.valueOf(uzaklik.get(aktifNode.getNodeID()).toString());
                enYakinNode = aktifNode;
            }
        }
        return enYakinNode;
    }      
    
    private List<Node> getKomsular(Node aktifNode) {            
        List<Node> komsular = new ArrayList<>();

        for (Edge aktifKenar : kenarlar) {
            if(ziyaretEdilmemis.contains(aktifNode) && aktifKenar != null) {
                if (aktifKenar.getBagliNodelar().get(0) == aktifNode) {
                    komsular.add(aktifKenar.getBagliNodelar().get(1));
                }
                if (aktifKenar.getBagliNodelar().get(1)== aktifNode) {
                    komsular.add(aktifKenar.getBagliNodelar().get(0));
                }
            }
        }
        return komsular;
    }

    private void uzaklikGuncelle(Node aktifNode,Node komsuNode){
        double alternatif = Double.valueOf(uzaklik.get(aktifNode.getNodeID()).toString()) + getAradakiUzaklik(aktifNode, komsuNode);
        if (alternatif < Double.valueOf(uzaklik.get(komsuNode.getNodeID()).toString())) {
            uzaklik.replace(komsuNode.getNodeID() , alternatif);
            oncelik.replace(komsuNode.getNodeID(), aktifNode);
        }
    }
             
    private double getAradakiUzaklik(Node baslangicNode, Node bitisNode) {
        for (Edge aktifKenar : kenarlar) {
            if (aktifKenar.getBagliNodelar().get(0).equals(baslangicNode) && aktifKenar.getBagliNodelar().get(1).equals(bitisNode)) {
                return aktifKenar.getUzunluk();
            }
            else if (aktifKenar.getBagliNodelar().get(1).equals(baslangicNode) && aktifKenar.getBagliNodelar().get(0).equals(bitisNode)) {
                return aktifKenar.getUzunluk();
            }
        }
        return 0;
    }
    
    public double getToplamUzaklik(Node bitisNode){
        double mesafe = Double.valueOf(uzaklik.get(bitisNode.getNodeID()).toString());
        return mesafe;
    }
    
    public List<Node> enKisaYoluOlustur(Node bitisNode) {
        List<Node> path = new ArrayList<>();
        path.add(bitisNode); 
        Node aktifNode = bitisNode;
        while (oncelik.get(aktifNode.getNodeID()) != null) {
            aktifNode = (Node)oncelik.get(aktifNode.getNodeID());
            path.add(0,aktifNode);
        }
        return path;
    }
      
}