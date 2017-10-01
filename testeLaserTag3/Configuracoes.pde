void saveConfigs() {

  configuracoes = createWriter("data/configs.txt");

  configuracoes.println(a.x + "\t" + a.y);
  configuracoes.println(b.x + "\t" + b.y);
  configuracoes.println(c.x + "\t" + c.y);
  configuracoes.println(d.x + "\t" + d.y);
  configuracoes.println(pixelRefR + "\t" + pixelRefG + "\t" + pixelRefB);
  configuracoes.flush();
  configuracoes.close();
}

void loadConfigs() {

  String[] lines;
  String[] pos;

  lines = loadStrings("data/configs.txt");

  pos = split(lines[0], '\t');
  a.x = int(pos[0]);
  a.y = int(pos[1]);
  p.valorXY(int(a.x), int(a.y), 0);

  pos = split(lines[1], '\t');
  b.x = int(pos[0]);
  b.y = int(pos[1]);
  p.valorXY(int(b.x), int(b.y), 1);

  pos = split(lines[2], '\t');
  c.x = int(pos[0]);
  c.y = int(pos[1]);
  p.valorXY(int(c.x), int(c.y), 2);

  pos = split(lines[3], '\t');
  d.x = int(pos[0]);
  d.y = int(pos[1]);
  p.valorXY(int(d.x), int(d.y), 3);

  pos = split(lines[4], '\t');
  pixelRefR = float(pos[0]);
  pixelRefG = float(pos[1]);
  pixelRefB = float(pos[2]);
}