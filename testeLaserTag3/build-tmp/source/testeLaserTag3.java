import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class testeLaserTag3 extends PApplet {



PrintWriter configuracoes;

Poly p; //area de interesse da cam
PVector a, b, c, d;

PGraphics pg;

Capture video;
PImage sprite; //imagem do spray
float pixelRefR, pixelRefG, pixelRefB;
boolean calibra=false, pinta=false;
//color cor = color(0, 255, 0);
int[] cor = new int[3]; 
int iC=0;

int corBorda = color(0, 0, 255, 40);

public void setup() {
  size(displayWidth, displayHeight, P3D);

  pg = createGraphics(640, 480, P3D);

  cor[0] = color(255, 0, 0);
  cor[1] = color(0, 255, 0);
  cor[2] = color(0, 0, 255);

  String[] cameras = Capture.list();

  if (cameras.length == 0) {
    println("There are no cameras available for capture.");
    exit();
  } 
  else {
    println("Available cameras:");
    for (int i = 0; i < cameras.length; i++) {
      println(cameras[i]);
    }
  }
  video = new Capture(this, cameras[0]);
  // video = new Capture(this, 640, 480);

  video.start(); 

  sprite = loadImage("wJxI64w.png"); //spray
  noStroke();
  smooth();
  background(0);

  int[] xP = { 
    0, width, width, 0
  }; //x pontos do poligono area de interesse do video

  int[] yP = { 
    0, 0, height, height
  }; //y pontos do poligono area de interesse do video

  p = new Poly(xP, yP, 4); //construtor classe polygon - java

  a = new PVector(xP[0], yP[0]);
  b = new PVector(xP[1], yP[1]);
  c = new PVector(xP[2], yP[2]);
  d = new PVector(xP[3], yP[3]);
}

public void draw() {

  int brightestX = width+100;
  int brightestY = height+100;

  if (video.available()) {
    video.read();
    if (calibra) {
      cursor(CROSS);
      image(video, 0, 0);
      background(255);
    } 
    else {
      noCursor();
    }

    video.loadPixels();
    int index = 0;
    for (int y = 0; y < video.height; y++) {
      for (int x = 0; x < video.width; x++) {

        int pixelValue = video.pixels[index];

        if (p.contains(x, y)) { //verifica se o pixel esta dentro do poligono
          if (calibra) { 
            set(x, y, pixelValue); //atribui o valor do pixel da cam ao do poligono
          } //atribui o valor do pixel da cam ao do poligono

        } 
        else if (calibra) {
          set(x, y, corBorda);
        }
        index++;
      }
    }
  }

  telaPlana(video, a, b, c, d);

  pg.loadPixels();
  int indexpg = 0;
  for (int ypg = 0; ypg < pg.height; ypg++) {
    for (int xpg = 0; xpg < pg.width; xpg++) {

      int pixelValuePg = pg.pixels[indexpg];

      float pixelRed = red(pixelValuePg);
      float pixelGreen = green(pixelValuePg);
      float pixelBlue = blue(pixelValuePg);

      if (pixelsComp(pixelRed, pixelGreen, pixelBlue)) { //testa se tem os valores de rgb do calibra
        brightestX = PApplet.parseInt(map(xpg, 0, pg.width, 0, width)); //transpoe os valores de captura para o valor de saida da tela
        brightestY = PApplet.parseInt(map(ypg, 0, pg.height, 0, height));
      }
      indexpg++;
    }
    pincel(brightestX, brightestY); //pinta a tela
  }

  if (calibra) { 
    p.drawMe(); //desenha o contorno do poligono
    //  p.drawMe2();
    //  text(p.polyArea(), mouseX, mouseY); 
    image(pg, 645, 0);
  }
}

public void pincel(int col, int lin) { //pinta a tela
  pushStyle();
  tint(cor[iC]);
  imageMode(CENTER);
  image(sprite, col, lin, 30, 30);
  popStyle();
}

public boolean pixelsComp(float r, float g, float b) { //compara os pixels

  if (r==pixelRefR && g==pixelRefG && b==pixelRefB) {
    return true;
  }

  return false;
}

public void telaPlana(PImage m, PVector c1, PVector c2, PVector c3, PVector c4) {
  pg.beginDraw();
  pg.beginShape();
  pg.texture(m);
  pg.vertex(0, 0, 0, c1.x, c1.y);
  pg.vertex(640, 0, 0, c2.x, c2.y);
  pg.vertex(640, 480, 0, c3.x, c3.y);
  pg.vertex(0, 480, 0, c4.x, c4.y);
  pg.endShape();
  pg.endDraw();
}

public void keyPressed() {
  switch(key) {
  case 'c': //calibrar o tracking com o mousePressed
    calibra=!calibra;
    background(0);
    break;

  case 't': //limpa a tela
    background(0);
    break;

  case 'r': //mudar cor da tinta
    //cor = color(255, 0, 0);
    iC=0;
    break;
  case 'g':
    // cor = color(0, 255, 0);
    iC=1;
    break;
  case 'b':
    //cor = color(0, 0, 255);
    iC=2;
    break;
  case 'm':
    pixelRefR = red(get(mouseX, mouseY));
    pixelRefG = green(get(mouseX, mouseY));
    pixelRefB = blue(get(mouseX, mouseY));
    break;
  case 's':
    saveConfigs();
    break;
  case 'l':
    loadConfigs();
    break;

  case '1':
    p.valorXY(mouseX, mouseY, 0);
    a.x=mouseX; 
    a.y=mouseY;   
    background(0);

    break;
  case '2':
    p.valorXY(mouseX, mouseY, 1); 
    b.x=mouseX; 
    b.y=mouseY;      
    background(0);

    break;
  case '3':
    p.valorXY(mouseX, mouseY, 2); 
    c.x=mouseX; 
    c.y=mouseY;      
    background(0);

    break;
  case '4':
    p.valorXY(mouseX, mouseY, 3); 
    d.x=mouseX; 
    d.y=mouseY;      
    background(0);

    break;
  }
}

public void mousePressed() {
  if (mouseButton == LEFT) {
    background(0);
  } 
  else if (mouseButton == RIGHT) {
    if (iC<2) {
      iC++;
    } 
    else {
      iC=0;
    }
  }
}

public void saveConfigs() {

  configuracoes = createWriter("data/configs.txt");

  configuracoes.println(a.x + "\t" + a.y);
  configuracoes.println(b.x + "\t" + b.y);
  configuracoes.println(c.x + "\t" + c.y);
  configuracoes.println(d.x + "\t" + d.y);
  configuracoes.println(pixelRefR + "\t" + pixelRefG + "\t" + pixelRefB);
  configuracoes.flush();
  configuracoes.close();
}

public void loadConfigs() {

  String[] lines;
  String[] pos;

  lines = loadStrings("data/configs.txt");

  pos = split(lines[0], '\t');
  a.x = PApplet.parseInt(pos[0]);
  a.y = PApplet.parseInt(pos[1]);
  p.valorXY(PApplet.parseInt(a.x), PApplet.parseInt(a.y), 0);

  pos = split(lines[1], '\t');
  b.x = PApplet.parseInt(pos[0]);
  b.y = PApplet.parseInt(pos[1]);
  p.valorXY(PApplet.parseInt(b.x), PApplet.parseInt(b.y), 1);

  pos = split(lines[2], '\t');
  c.x = PApplet.parseInt(pos[0]);
  c.y = PApplet.parseInt(pos[1]);
  p.valorXY(PApplet.parseInt(c.x), PApplet.parseInt(c.y), 2);

  pos = split(lines[3], '\t');
  d.x = PApplet.parseInt(pos[0]);
  d.y = PApplet.parseInt(pos[1]);
  p.valorXY(PApplet.parseInt(d.x), PApplet.parseInt(d.y), 3);

  pos = split(lines[4], '\t');
  pixelRefR = PApplet.parseFloat(pos[0]);
  pixelRefG = PApplet.parseFloat(pos[1]);
  pixelRefB = PApplet.parseFloat(pos[2]);
}

/*

 The class inherit all the fields, constructors and functions 
 
 of the java.awt.Polygon class, including contains(), xpoint,ypoint,npoint
 
 */

class Poly extends java.awt.Polygon {


  public Poly(int[] x, int[] y, int n) {

    //call the java.awt.Polygon constructor
    super(x, y, n);
  }

  public void valorXY(int a, int b, int index) {
    xpoints[index]=a;
    ypoints[index]=b;
  }

  public void drawMe() {

    pushStyle();
    noFill();
    stroke(0, 0, 255);
    beginShape();

    for (int i = 0; i < npoints; i++) {

      vertex(xpoints[i], ypoints[i]);
    }

    endShape(CLOSE);
    popStyle();
  }
  
    public void drawMe2() {
    pushStyle();
    noFill();
    stroke(0, 0, 255);
    beginShape();

    for (int i = 0; i < npoints; i++) {

      vertex(xpoints[i]+640, ypoints[i]);
    }

    endShape(CLOSE);
    popStyle();
  }
  
  public int polyArea(){ //retorna o valor em numero de pixels
  int area = 0;
  int j = npoints-1; //o \u00faltimo vertice \u00e9 o anterior do primeiro
  for(int i = 0; i<npoints; i++){
  area = area + (xpoints[j]+xpoints[i]) * (ypoints[j]-ypoints[i]);
  j = i; //j \u00e9 o vertice anterior ao i
  }
  area = area/2;
  return area;
  }

}



  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "testeLaserTag3" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
