import processing.video.*; //resolver problema com essa biblioteca no Linux: sudo apt-get install gstreamer0.10-plugins-good
//https://forum.processing.org/two/discussion/15698/tutorial-installing-processing-3-x-on-ubuntu-linux-computers

PrintWriter configuracoes;

Poly p; //area de interesse da cam
PVector a, b, c, d;

PGraphics pg;

Capture video;
PImage sprite; //imagem do spray
float pixelRefR, pixelRefG, pixelRefB;
boolean calibra=false, pinta=false;
//color cor = color(0, 255, 0);
color[] cor = new color[3]; 
int iC=0;

color corBorda = color(0, 0, 255, 40);

void setup() {
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

void draw() {

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
        brightestX = int(map(xpg, 0, pg.width, 0, width)); //transpoe os valores de captura para o valor de saida da tela
        brightestY = int(map(ypg, 0, pg.height, 0, height));
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

void pincel(int col, int lin) { //pinta a tela
  pushStyle();
  tint(cor[iC]);
  imageMode(CENTER);
  image(sprite, col, lin, 30, 30);
  popStyle();
}

boolean pixelsComp(float r, float g, float b) { //compara os pixels

  if (r==pixelRefR && g==pixelRefG && b==pixelRefB) {
    return true;
  }

  return false;
}

void telaPlana(PImage m, PVector c1, PVector c2, PVector c3, PVector c4) {
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

void keyPressed() {
  switch(key) {
  case 'c': //entra no modo de calibrar o tracking com o mousePressed
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
  case 'm': //obtem a cor do laser q sera rastreado
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

//define os quatro cantos do poligono

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

void mousePressed() {
  if (mouseButton == LEFT) { //limpa tela
    background(0);
  } 
  else if (mouseButton == RIGHT) { //muda cor da tinta
    if (iC<2) {
      iC++;
    } 
    else {
      iC=0;
    }
  }
}