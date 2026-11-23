package com.kiet13312.chatlgbtgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class GameView extends View {
    private static final float W=960,H=540,WORLD_W=2400,WORLD_H=1600;
    private static final int WHITE=Color.WHITE, DARK=Color.rgb(20,37,60), GOLD=Color.rgb(255,193,56), GREEN=Color.rgb(56,169,105), BLUE=Color.rgb(57,133,218), PURPLE=Color.rgb(139,91,205), RED=Color.rgb(220,79,75);
    private final Paint p=new Paint(3); private final RectF rr=new RectF();
    private final SharedPreferences save; private final Random rng=new Random();
    private final Bitmap gardenArt,fishArt,studioArt;
    private String screen="WORLD",notice="Chào mừng đến với Chat LGBT Game!",plantPick="PEA";
    private int coins,gems,energy,xp,level,rod,character,dayFish,miniScore,wood=8,stone=5,blocks;
    private boolean giftClaimed,paused,over,hooked,casting,reeling,studioRun,buildMode,heistWon;
    private int sun=200,kills,gardenLevel=1,fishKg=35,fishPct,heistGems,raceLane=1,raceTime,tankHp=3,tankPts,variable=1;
    private float t,last,spawn,sunTick,fishWait,fishX=610,fishY=310,guardX=760,heroX=180,heroY=430,studioX=770;
    private final Plant[][] garden=new Plant[5][9];
    private final boolean[] blocksPlaced=new boolean[40];
    private final List<Zombie> zombies=new ArrayList<>();
    private final List<Bullet> bullets=new ArrayList<>();
    private final List<Foe> foes=new ArrayList<>();
    private final List<String> code=new ArrayList<>();
    private float scale=1,ox,oy;
    private float worldX=1100,worldY=760,targetX=1100,targetY=760;
    private boolean moving;
    private int nearby=-1;
    private String arcadeType="";
    private int arcadeScore,arcadeRound,selectedTile=-1,keeperLane=1,petHunger=45,petHappy=55,petClean=60,misses;
    private float noteY=-35,tagEnemyX=700,tagEnemyY=330;
    private final int[] puzzleTiles=new int[16];
    private final int[] farmCrop=new int[6];
    private static final class Landmark {
        final float x,y; final String name,subtitle,mode; final int color;
        Landmark(float x,float y,String name,String subtitle,String mode,int color){this.x=x;this.y=y;this.name=name;this.subtitle=subtitle;this.mode=mode;this.color=color;}
    }
    private final Landmark[] landmarks={
        new Landmark(350,300,"GARDEN DEFENSE","Vườn cây chiến thuật","GARDEN",GREEN),
        new Landmark(900,250,"CÂU CÁ VẠN CÂN","Hồ câu cá & nâng cần","FISH",BLUE),
        new Landmark(1580,250,"SCRATCHBETTER","Xưởng sáng tạo game","STUDIO",PURPLE),
        new Landmark(2010,320,"SÂN THỂ THAO","Sút bóng ghi điểm","SPORT",GREEN),
        new Landmark(650,620,"PUZZLE PARK","Ghép màu & giải đố","PUZZLE",PURPLE),
        new Landmark(1320,500,"NHẠC HỘI","Nhịp điệu & phản xạ","RHYTHM",Color.rgb(219,94,153)),
        new Landmark(250,880,"RUNNER TRAIL","Chạy vượt chướng ngại","RUNNER",GOLD),
        new Landmark(880,1110,"TANK TRACK","Đường đua xe tăng","TANK",BLUE),
        new Landmark(420,1290,"SPEED CIRCUIT","Đua xe 3 làn","RACE",RED),
        new Landmark(360,1480,"PET VILLAGE","Chăm sóc thú cưng","PET",Color.rgb(232,143,177)),
        new Landmark(1300,1370,"FARM & TYCOON","Nông trại và cửa hàng","FARM",GREEN),
        new Landmark(1800,1050,"PIXEL ISLAND","Sinh tồn & xây dựng","SURVIVAL",Color.rgb(145,112,75)),
        new Landmark(2110,1200,"GEM CITY","Mê cung tìm kho báu","HEIST",Color.rgb(170,118,68)),
        new Landmark(1950,680,"ARCADE PLAZA","Mini game tổng hợp","MINI",GOLD),
        new Landmark(2210,450,"STRATEGY CASTLE","Chiến thuật thủ thành","MOBA",PURPLE),
        new Landmark(1120,950,"COLOR TAG ARENA","Sinh tồn né chạm","TAG",RED),
        new Landmark(1660,1450,"BUILD & CREATE","Sandbox tự do","SURVIVAL",GREEN),
        new Landmark(1030,570,"TOWN TYCOON","Xây dựng thị trấn","TYCOON",Color.rgb(205,151,67))
    };

    private static final class Plant { String type; int hp; float cd; Plant(String t){type=t;hp=t.equals("NUT")?220:70;} }
    private static final class Zombie { int row,hp=100;float x,bite;Zombie(int r){row=r;x=790;} }
    private static final class Bullet {int row;float x;Bullet(int r,float x){row=r;this.x=x;} }
    private static final class Foe {float x,y,s;Foe(float a,float b,float c){x=a;y=b;s=c;} }

    public GameView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);save=c.getSharedPreferences("chat_lgbt_game",Context.MODE_PRIVATE);
        coins=save.getInt("coins",1200);gems=save.getInt("gems",40);energy=save.getInt("energy",100);xp=save.getInt("xp",0);level=save.getInt("level",1);rod=save.getInt("rod",1);character=save.getInt("character",0);gardenLevel=save.getInt("gardenLevel",1);dayFish=save.getInt("dayFish",0);giftClaimed=save.getBoolean("gift",false);variable=save.getInt("variable",1);
        gardenArt=vector(R.drawable.garden_art);fishArt=vector(R.drawable.fishing_art);studioArt=vector(R.drawable.studio_art);
        worldX=save.getFloat("worldX",1100);worldY=save.getFloat("worldY",760);targetX=worldX;targetY=worldY;
        for(int i=0;i<puzzleTiles.length;i++)puzzleTiles[i]=i/4;
        setFocusable(true);}
    private Bitmap vector(int id){try{Drawable d=getResources().getDrawable(id,null);Bitmap b=Bitmap.createBitmap(720,480,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);d.setBounds(0,0,720,480);d.draw(c);return b;}catch(Exception e){return null;}}
    private void persist(){save.edit().putInt("coins",coins).putInt("gems",gems).putInt("energy",energy).putInt("xp",xp).putInt("level",level).putInt("rod",rod).putInt("character",character).putInt("gardenLevel",gardenLevel).putInt("dayFish",dayFish).putBoolean("gift",giftClaimed).putInt("variable",variable).putFloat("worldX",worldX).putFloat("worldY",worldY).apply();}

    @Override protected void onDraw(Canvas c){super.onDraw(c);scale=Math.min(getWidth()/W,getHeight()/H);ox=(getWidth()-W*scale)/2;oy=(getHeight()-H*scale)/2;c.save();c.translate(ox,oy);c.scale(scale,scale);long now=System.nanoTime();float dt=last==0?.016f:Math.min(.05f,(now-last)/1e9f);last=now;tick(dt);
        switch(screen){case "HUB":case "WORLD":worldScreen(c);break;case "GARDEN":gardenScreen(c);break;case "FISH":fishScreen(c);break;case "STUDIO":studioScreen(c);break;case "TANK":tankScreen(c);break;case "HEIST":heistScreen(c);break;case "SURVIVAL":survivalScreen(c);break;case "RACE":raceScreen(c);break;case "MINI":miniScreen(c);break;case "ARCADE":arcadeScreen(c);break;case "SHOP":shopScreen(c);break;case "BAG":bagScreen(c);break;case "TASK":taskScreen(c);break;default:settingsScreen(c);}c.restore();postInvalidateDelayed(32);}
    private void tick(float d){if(screen.equals("GARDEN")&&!paused&&!over)gardenTick(d);if(screen.equals("FISH")&&!over)fishTick(d);if(screen.equals("STUDIO")&&studioRun){studioX+=d*(35+variable*5);if(studioX>915)studioX=705;}if(screen.equals("TANK")&&!over)tankTick(d);if(screen.equals("HEIST")&&!over){guardX-=d*14;if(guardX<220)guardX=760;if(Math.abs(guardX-heroX)<25&&Math.abs(heroY-430)<42)over=true;}if(screen.equals("RACE")&&!over)raceTick(d);if(screen.equals("WORLD"))worldTick(d);if(screen.equals("ARCADE"))arcadeTick(d);}
    private void bg(Canvas c,int a,int b){p.setShader(new LinearGradient(0,0,0,H,a,b,Shader.TileMode.CLAMP));c.drawRect(0,0,W,H,p);p.setShader(null);}
    private void txt(Canvas c,String s,float x,float y,float z,int color,boolean bold,Paint.Align align){p.setColor(color);p.setTextSize(z);p.setTextAlign(align);p.setTypeface(Typeface.create("sans-serif-rounded",bold?Typeface.BOLD:Typeface.NORMAL));p.setStyle(Paint.Style.FILL);c.drawText(s,x,y,p);}
    private void box(Canvas c,float x,float y,float w,float h,int color,float rad){rr.set(x,y,x+w,y+h);p.setColor(color);p.setStyle(Paint.Style.FILL);p.clearShadowLayer();c.drawRoundRect(rr,rad,rad,p);p.setColor(Color.argb(45,255,255,255));p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);c.drawRoundRect(rr,rad,rad,p);p.setStyle(Paint.Style.FILL);}
    private void btn(Canvas c,String s,float x,float y,float w,float h,int color){box(c,x+2,y+4,w,h,Color.argb(65,0,0,0),12);box(c,x,y,w,h,color,12);txt(c,s,x+w/2,y+h/2+6,16,WHITE,true,Paint.Align.CENTER);}
    private void art(Canvas c,Bitmap b,float x,float y,float w,float h){if(b==null)return;c.save();rr.set(x,y,x+w,y+h);Path path=new Path();path.addRoundRect(rr,14,14,Path.Direction.CW);c.clipPath(path);float sc=Math.max(w/b.getWidth(),h/b.getHeight());float sw=w/sc,sh=h/sc;Rect src=new Rect((int)((b.getWidth()-sw)/2),(int)((b.getHeight()-sh)/2),(int)((b.getWidth()+sw)/2),(int)((b.getHeight()+sh)/2));c.drawBitmap(b,src,new RectF(x,y,x+w,y+h),p);c.restore();}
    private void header(Canvas c,String title){box(c,0,0,W,68,Color.argb(235,17,34,59),0);btn(c,"‹",14,12,50,43,Color.rgb(75,94,117));txt(c,title,82,42,23,WHITE,true,Paint.Align.LEFT);txt(c,"Lv."+level+"    🪙 "+coins,810,40,16,GOLD,true,Paint.Align.CENTER);}
    private void top(Canvas c){box(c,15,12,270,55,Color.argb(225,20,42,67),15);txt(c,"K  KIỆT  •  Lv."+level,30,35,17,WHITE,true,Paint.Align.LEFT);txt(c,xp+" / "+(level*100)+" XP",72,55,11,WHITE,false,Paint.Align.LEFT);box(c,700,17,110,36,DARK,14);txt(c,"🪙 "+coins,755,41,15,GOLD,true,Paint.Align.CENTER);box(c,820,17,115,36,DARK,14);txt(c,"💎 "+gems,878,41,15,Color.rgb(120,220,255),true,Paint.Align.CENTER);}
    private void earn(int amount,int exp){coins+=amount;xp+=exp;if(xp>=level*100){xp-=level*100;level++;gems+=2;notice="Lên cấp "+level+"! +2 kim cương";}persist();}

    private void hub(Canvas c){bg(c,Color.rgb(82,166,225),Color.rgb(99,178,115));p.setColor(Color.rgb(68,155,105));c.drawOval(-80,330,530,640,p);c.drawOval(430,350,1050,650,p);top(c);
        btn(c,"SHOP",15,90,90,46,PURPLE);btn(c,"KHO ĐỒ",15,145,90,46,BLUE);btn(c,"NHIỆM VỤ",15,200,90,46,GREEN);btn(c,"CÀI ĐẶT",15,255,90,46,Color.rgb(85,105,127));
        box(c,125,82,820,55,Color.argb(220,20,42,66),15);txt(c,"CHAT LGBT GAME",148,108,24,WHITE,true,Paint.Align.LEFT);txt(c,"Một thế giới • Nhiều trò chơi • Tiến trình dùng chung",148,128,13,Color.rgb(218,235,255),false,Paint.Align.LEFT);txt(c,"⚡ "+energy+"/100",855,116,16,WHITE,true,Paint.Align.CENTER);
        mode(c,125,151,257,160,gardenArt,"GARDEN DEFENSE","Trồng cây • Chặn quái",GREEN);mode(c,394,151,257,160,fishArt,"CÂU CÁ VẠN CÂN","Thả câu • Kéo cá • Nâng cần",BLUE);mode(c,663,151,282,160,studioArt,"SCRATCHBETTER STUDIO","Lắp khối lệnh • Tạo trò chơi",PURPLE);
        tile(c,125,324,196,116,"TANK ADVENTURES","Lái xe tăng • Bắn mục tiêu","TANK",BLUE);tile(c,329,324,196,116,"FURNI HEIST","Thu thập đá quý • Thoát","◆",Color.rgb(170,118,68));tile(c,533,324,196,116,"SINH TỒN KHỐI","Đào • Chế tạo • Xây dựng","▦",GREEN);tile(c,737,324,208,116,"ĐUA XE & MINI","Đua xe • Phản xạ","★",RED);
        box(c,125,454,820,65,Color.argb(220,20,42,66),15);txt(c,"Đăng nhập mỗi ngày",148,481,16,WHITE,true,Paint.Align.LEFT);txt(c,notice,148,503,12,Color.rgb(213,228,245),false,Paint.Align.LEFT);btn(c,giftClaimed?"ĐÃ NHẬN":"🎁 NHẬN QUÀ",756,465,170,43,GOLD);}
    private void mode(Canvas c,float x,float y,float w,float h,Bitmap b,String title,String sub,int col){box(c,x,y,w,h,WHITE,17);art(c,b,x+4,y+4,w-8,h-8);p.setColor(Color.argb(180,8,24,42));c.drawRoundRect(x+4,y+h-57,x+w-4,y+h-4,12,12,p);txt(c,title,x+15,y+h-31,17,WHITE,true,Paint.Align.LEFT);txt(c,sub,x+15,y+h-12,11,WHITE,false,Paint.Align.LEFT);box(c,x+w-42,y+12,30,30,col,15);txt(c,"▶",x+w-27,y+33,14,WHITE,true,Paint.Align.CENTER);}
    private void tile(Canvas c,float x,float y,float w,float h,String title,String sub,String icon,int col){box(c,x,y,w,h,Color.argb(235,255,255,255),16);box(c,x+7,y+8,50,h-16,col,12);txt(c,icon,x+32,y+65,25,WHITE,true,Paint.Align.CENTER);txt(c,title,x+67,y+39,14,DARK,true,Paint.Align.LEFT);txt(c,sub,x+67,y+63,10,Color.rgb(80,99,115),false,Paint.Align.LEFT);}
    private void startGarden(){screen="GARDEN";sun=200;kills=0;spawn=1.2f;sunTick=4;paused=false;over=false;t=0;zombies.clear();bullets.clear();for(int i=0;i<5;i++)Arrays.fill(garden[i],null);}
    private void gardenTick(float d){t+=d;spawn-=d;sunTick-=d;if(sunTick<=0){sun+=25;sunTick=6;}if(spawn<=0&&zombies.size()<12&&kills<15){zombies.add(new Zombie(rng.nextInt(5)));spawn=Math.max(2.2f,5.8f-gardenLevel*.2f);}for(int row=0;row<5;row++)for(int col=0;col<9;col++){Plant a=garden[row][col];if(a==null)continue;a.cd-=d;if(a.type.equals("SUN")&&a.cd<=0){sun+=25;a.cd=8;}if(a.type.equals("PEA")&&a.cd<=0){boolean has=false;for(Zombie z:zombies)if(z.row==row&&z.x>160)has=true;if(has){bullets.add(new Bullet(row,180+col*64));a.cd=1.35f;}}}for(Bullet b:bullets)b.x+=260*d;Iterator<Bullet> bi=bullets.iterator();while(bi.hasNext()){Bullet b=bi.next();boolean hit=false;for(Zombie z:zombies)if(z.row==b.row&&Math.abs(z.x-b.x)<20){z.hp-=35;hit=true;break;}if(hit||b.x>800)bi.remove();}Iterator<Zombie> zi=zombies.iterator();while(zi.hasNext()){Zombie z=zi.next();int col=Math.max(0,Math.min(8,(int)((z.x-180)/64)));Plant a=garden[z.row][col];if(a!=null&&z.x<200+col*64+34){z.bite+=d;if(z.bite>.7f){a.hp-=15;z.bite=0;if(a.hp<=0)garden[z.row][col]=null;}}else z.x-=24*d;if(z.hp<=0){zi.remove();kills++;coins+=6;}else if(z.x<150){over=true;paused=true;}}if(kills>=15&&!over){over=true;gardenLevel=Math.min(9,gardenLevel+1);energy=Math.min(100,energy+5);earn(180,45);}}
    private void gardenScreen(Canvas c){art(c,gardenArt,0,0,W,H);box(c,0,0,W,68,Color.argb(180,13,34,52),0);btn(c,"‹",14,12,50,43,BLUE);txt(c,"GARDEN DEFENSE  •  MÀN "+gardenLevel,82,41,21,WHITE,true,Paint.Align.LEFT);txt(c,"☀ "+sun+"     Hạ "+kills+"/15",790,41,17,GOLD,true,Paint.Align.CENTER);box(c,12,82,142,360,Color.argb(220,20,43,66),15);String[] n={"SUN","PEA","NUT"};int[] cost={50,100,75},col={GOLD,GREEN,Color.rgb(155,108,69)};for(int i=0;i<3;i++){int yy=100+i*102;box(c,22,yy,122,88,plantPick.equals(n[i])?Color.rgb(255,235,176):Color.rgb(58,78,96),12);p.setColor(col[i]);c.drawCircle(54,yy+35,21,p);txt(c,n[i],89,yy+34,14,WHITE,true,Paint.Align.LEFT);txt(c,cost[i]+" ☀",89,yy+57,12,WHITE,false,Paint.Align.LEFT);}btn(c,paused?"TIẾP TỤC":"TẠM DỪNG",22,410,122,38,Color.rgb(82,103,123));for(int row=0;row<5;row++)for(int coln=0;coln<9;coln++){float x=170+coln*64,y=111+row*62;box(c,x,y,62,59,Color.argb(75,170,230,122),7);Plant a=garden[row][coln];if(a!=null){p.setColor(a.type.equals("SUN")?GOLD:a.type.equals("PEA")?GREEN:Color.rgb(155,109,69));c.drawCircle(x+31,y+30,22,p);txt(c,a.type.equals("SUN")?"☀":a.type.equals("PEA")?"●":"⬟",x+31,y+38,24,WHITE,true,Paint.Align.CENTER);}}for(Bullet b:bullets){p.setColor(Color.rgb(184,255,104));c.drawCircle(b.x,142+b.row*62,7,p);}for(Zombie z:zombies){p.setColor(Color.rgb(95,157,102));c.drawCircle(z.x,142+z.row*62,19,p);p.setColor(WHITE);c.drawCircle(z.x-7,136+z.row*62,4,p);c.drawCircle(z.x+7,136+z.row*62,4,p);}if(over)result(c,kills>=15?"CHIẾN THẮNG":"VƯỜN BỊ TRÀN","Hạ được "+kills+" quái",kills>=15);}
    
    private void startFish(){screen="FISH";fishPct=0;hooked=false;casting=false;reeling=false;over=false;t=0;fishWait=0;fishKg=10+rng.nextInt(91);fishX=600;}
    private void fishTick(float d){t+=d;fishX+=Math.sin(t*1.4)*14*d;fishY=300+(float)Math.cos(t)*9;if(casting){fishWait+=d;if(fishWait>2.2f){casting=false;hooked=true;}}if(hooked&&reeling)fishPct+=Math.max(1,(int)(d*(8+rod*2)));if(fishPct>=100){over=true;hooked=false;reeling=false;int reward=fishKg*(3+rod);dayFish++;energy=Math.max(0,energy-4);earn(reward,fishKg/2);}}
    private void fishScreen(Canvas c){art(c,fishArt,0,0,W,H);box(c,0,0,W,68,Color.argb(170,8,37,68),0);btn(c,"‹",14,12,50,43,BLUE);txt(c,"CÂU CÁ VẠN CÂN",82,41,22,WHITE,true,Paint.Align.LEFT);txt(c,"Cần cấp "+rod+" • "+new String[]{"Sở Tâm","Bá Thường","Lão Ngô"}[character],775,40,15,WHITE,true,Paint.Align.CENTER);p.setColor(Color.rgb(255,243,190));p.setStrokeWidth(2.5f);c.drawLine(480,175,625,305,p);p.setColor(Color.rgb(244,94,83));c.drawCircle(625,305,8,p);p.setColor(Color.rgb(243,146,69));c.drawOval(fishX-34,fishY-14,fishX+34,fishY+14,p);p.setColor(WHITE);c.drawCircle(fishX+17,fishY-4,4,p);box(c,18,90,220,78,Color.argb(215,19,45,69),14);txt(c,"CÁ MỤC TIÊU",35,116,13,WHITE,true,Paint.Align.LEFT);txt(c,fishKg+" KG",35,151,27,GOLD,true,Paint.Align.LEFT);if(hooked){box(c,345,455,270,19,DARK,9);box(c,345,455,270*Math.min(1,fishPct/100f),19,GREEN,9);txt(c,"ĐANG KÉO "+fishPct+"%",480,445,13,WHITE,true,Paint.Align.CENTER);}if(!casting&&!hooked&&!over)btn(c,"🎣 THẢ CÂU",750,405,175,52,BLUE);if(casting)btn(c,"ĐỢI CÁ CẮN...",750,405,175,52,Color.GRAY);if(hooked)btn(c,"GIỮ KÉO!",750,405,175,52,GREEN);btn(c,"ĐỔI NHÂN VẬT",750,468,175,40,PURPLE);if(over)result(c,"CÂU THÀNH CÔNG!","Cá "+fishKg+" kg • Nhận "+(fishKg*(3+rod))+" xu",true);else txt(c,"Thả câu rồi chạm/giữ nút kéo để thu dây",470,518,14,WHITE,true,Paint.Align.CENTER);}
    
    private void startStudio(){screen="STUDIO";code.clear();String saved=save.getString("script","");if(!saved.isEmpty())code.addAll(Arrays.asList(saved.split("\\n")));if(code.isEmpty()){code.add("Khi bấm cờ xanh");code.add("Di chuyển 10 bước");code.add("Nói Xin chào");}studioX=770;studioRun=false;}
    private void studioScreen(Canvas c){bg(c,Color.rgb(64,148,211),Color.rgb(54,79,138));art(c,studioArt,0,68,W,H-68);box(c,0,0,W,68,Color.argb(195,13,29,52),0);btn(c,"‹",14,12,50,43,BLUE);txt(c,"SCRATCHBETTER STUDIO",82,41,22,WHITE,true,Paint.Align.LEFT);btn(c,studioRun?"DỪNG":"▶ CHẠY",755,12,105,43,studioRun?RED:GREEN);btn(c,"LƯU",870,12,75,43,BLUE);box(c,12,82,180,430,Color.argb(230,20,39,68),14);txt(c,"KHỐI LỆNH",28,108,16,WHITE,true,Paint.Align.LEFT);String[] b={"Di chuyển 10 bước","Xoay 15 độ","Nói Xin chào","Đợi 1 giây","Lặp lại 5 lần","Đổi màu","Tăng biến"};int[] colors={BLUE,PURPLE,GOLD,GREEN,RED,Color.MAGENTA,Color.rgb(173,119,63)};for(int i=0;i<7;i++)btn(c,b[i],23,121+i*49,158,40,colors[i]);box(c,205,82,470,430,Color.argb(225,25,42,63),14);txt(c,"KỊCH BẢN",225,109,16,WHITE,true,Paint.Align.LEFT);for(int i=0;i<code.size();i++){box(c,228,122+i*51,300,42,i==0?GREEN:BLUE,9);txt(c,code.get(i),244,149+i*51,14,WHITE,true,Paint.Align.LEFT);}btn(c,"XÓA CUỐI",230,443,142,42,RED);btn(c,"−",386,443,95,42,Color.GRAY);btn(c,"+",492,443,95,42,GREEN);box(c,690,82,258,430,Color.argb(225,20,40,64),14);txt(c,"SÂN KHẤU",710,109,16,WHITE,true,Paint.Align.LEFT);box(c,704,125,230,250,Color.rgb(194,227,237),12);p.setColor(GREEN);c.drawRect(704,312,934,375,p);p.setColor(GOLD);c.drawCircle(studioX-600,245,22,p);p.setColor(BLUE);rr.set(studioX-615,266,studioX-585,308);c.drawRoundRect(rr,8,8,p);txt(c,"Biến: "+variable,820,410,15,WHITE,true,Paint.Align.CENTER);txt(c,"Lắp các khối rồi bấm CHẠY",820,454,12,WHITE,false,Paint.Align.CENTER);}
    
    private void startTank(){screen="TANK";foes.clear();tankHp=3;tankPts=0;spawn=.5f;t=0;heroX=480;heroY=420;over=false;}
    private void tankTick(float d){t+=d;spawn-=d;if(spawn<=0){foes.add(new Foe(130+rng.nextInt(700),-30,65+rng.nextInt(50)));spawn=Math.max(.7f,2-t*.01f);}for(Foe f:foes)f.y+=f.s*d;Iterator<Foe>it=foes.iterator();while(it.hasNext()){Foe f=it.next();if(f.y>485){it.remove();if(--tankHp<=0)over=true;}}}
    private void tankScreen(Canvas c){bg(c,Color.rgb(40,79,102),Color.rgb(54,98,74));header(c,"TANK ADVENTURES");for(int x=0;x<960;x+=64)c.drawLine(x,70,x,540,p);txt(c,"HP "+tankHp+"  •  ĐIỂM "+tankPts,840,95,16,WHITE,true,Paint.Align.CENTER);p.setColor(GREEN);rr.set(heroX-28,heroY-24,heroX+28,heroY+24);c.drawRoundRect(rr,8,8,p);p.setColor(Color.rgb(43,91,60));rr.set(heroX-8,heroY-38,heroX+8,heroY+3);c.drawRoundRect(rr,5,5,p);for(Foe f:foes){p.setColor(RED);rr.set(f.x-21,f.y-18,f.x+21,f.y+18);c.drawRoundRect(rr,8,8,p);}btn(c,"◀",55,450,80,58,BLUE);btn(c,"▶",150,450,80,58,BLUE);btn(c,"BẮN",810,444,110,64,RED);if(over)result(c,"XE TĂNG HẾT HP","Điểm: "+tankPts,false);}
    
    private void startHeist(){screen="HEIST";heistGems=0;heroX=180;heroY=430;guardX=760;over=false;heistWon=false;}
    private void heistScreen(Canvas c){bg(c,Color.rgb(38,43,69),Color.rgb(91,72,75));header(c,"FURNI HEIST");for(int row=0;row<5;row++)for(int col=0;col<9;col++)box(c,95+col*86,93+row*78,80,72,(row+col)%2==0?Color.rgb(65,72,94):Color.rgb(76,78,100),9);for(int i=0;i<3;i++){p.setColor(i<heistGems?Color.GRAY:GOLD);c.drawCircle(230+i*180,170+(i%2)*130,12,p);}p.setColor(GREEN);c.drawCircle(875,470,24,p);txt(c,"EXIT",875,475,10,WHITE,true,Paint.Align.CENTER);p.setColor(BLUE);c.drawCircle(heroX,heroY,17,p);txt(c,"K",heroX,heroY+6,15,WHITE,true,Paint.Align.CENTER);p.setColor(RED);c.drawCircle(guardX,430,19,p);txt(c,"!",guardX,437,20,WHITE,true,Paint.Align.CENTER);txt(c,"Đá quý "+heistGems+"/3 • Chạm nền để di chuyển",480,512,14,WHITE,true,Paint.Align.CENTER);if(over)result(c,heistWon?"THOÁT THÀNH CÔNG":"BỊ BẮT RỒI",heistWon?"+150 xu":"Hãy thử đường khác",heistWon);}
    
    private void startSurvival(){screen="SURVIVAL";wood=8;stone=5;blocks=0;buildMode=false;Arrays.fill(blocksPlaced,false);}
    private void survivalScreen(Canvas c){bg(c,Color.rgb(113,188,236),Color.rgb(76,143,89));header(c,"SINH TỒN KHỐI");txt(c,"Gỗ "+wood+"   Đá "+stone+"   Khối "+blocks,480,96,17,WHITE,true,Paint.Align.CENTER);for(int row=0;row<5;row++)for(int col=0;col<8;col++){int i=row*8+col;float x=150+col*84,y=115+row*76;box(c,x,y,78,70,blocksPlaced[i]?Color.rgb(151,105,68):(i%3==0?Color.rgb(147,174,100):Color.rgb(98,157,93)),7);}box(c,830,118,112,280,DARK,14);btn(c,"ĐÀO GỖ",840,135,92,48,Color.rgb(153,108,68));btn(c,"ĐÀO ĐÁ",840,197,92,48,Color.GRAY);btn(c,"XÂY KHỐI",840,259,92,48,GREEN);btn(c,"CHẾ TẠO",840,321,92,48,PURPLE);txt(c,buildMode?"Chọn ô trống để đặt khối":"Thu thập vật liệu và xây dựng",520,510,15,WHITE,true,Paint.Align.CENTER);}
    
    private void startRace(){screen="RACE";foes.clear();raceLane=1;raceTime=0;spawn=.7f;t=0;over=false;}
    private void raceTick(float d){t+=d;raceTime=(int)t;spawn-=d;if(spawn<=0){foes.add(new Foe(310+rng.nextInt(3)*170,-25,135+rng.nextInt(55)));spawn=1;}Iterator<Foe>i=foes.iterator();while(i.hasNext()){Foe f=i.next();f.y+=f.s*d;if(f.y>500)i.remove();else if(Math.abs(f.x-(310+raceLane*170))<45&&Math.abs(f.y-410)<60)over=true;}}
    private void raceScreen(Canvas c){bg(c,Color.rgb(67,141,188),Color.rgb(57,104,84));header(c,"ĐUA XE");p.setColor(Color.rgb(52,57,63));rr.set(240,72,760,540);c.drawRoundRect(rr,12,12,p);p.setColor(WHITE);for(int y=75;y<540;y+=70){c.drawRect(409,y,414,y+34,p);c.drawRect(579,y,584,y+34,p);}float x=310+raceLane*170;p.setColor(BLUE);rr.set(x-28,382,x+28,454);c.drawRoundRect(rr,11,11,p);for(Foe f:foes){p.setColor(RED);rr.set(f.x-27,f.y-36,f.x+27,f.y+36);c.drawRoundRect(rr,10,10,p);}txt(c,"Sống sót "+raceTime+" giây",480,110,17,WHITE,true,Paint.Align.CENTER);btn(c,"◀",60,448,100,58,BLUE);btn(c,"▶",175,448,100,58,BLUE);if(over)result(c,"VA CHẠM!","Sống sót "+raceTime+" giây",false);}
    private void miniScreen(Canvas c){bg(c,Color.rgb(121,103,203),Color.rgb(67,105,164));header(c,"MINI GAME • PHẢN XẠ");txt(c,"CHẠM CÁC NGÔI SAO",480,155,28,WHITE,true,Paint.Align.CENTER);txt(c,"Điểm: "+miniScore,480,198,19,GOLD,true,Paint.Align.CENTER);for(int i=0;i<8;i++){float x=155+(i%4)*220,y=245+(i/4)*115;box(c,x,y,150,78,i%2==0?GOLD:GREEN,17);txt(c,"★",x+75,y+53,38,WHITE,true,Paint.Align.CENTER);}txt(c,"Chạm ô sao để ghi điểm",480,510,15,WHITE,true,Paint.Align.CENTER);}

    private void shopScreen(Canvas c){bg(c,Color.rgb(105,151,213),Color.rgb(72,86,139));header(c,"CỬA HÀNG");box(c,100,92,760,390,Color.argb(225,22,42,66),18);txt(c,"NÂNG CẤP DÙNG CHUNG",140,130,20,WHITE,true,Paint.Align.LEFT);shopRow(c,150,"Cần câu cấp "+rod,"Câu cá khỏe hơn",rod*250,"NÂNG CẤP");shopRow(c,245,"Năng lượng +25","Chơi thêm các màn",150,"MUA");shopRow(c,340,"Hộp kim cương","Nhận 5 kim cương",300,"MUA");}
    private void shopRow(Canvas c,float y,String title,String sub,int cost,String b){box(c,130,y,700,78,Color.rgb(43,67,94),13);txt(c,title,155,y+29,17,WHITE,true,Paint.Align.LEFT);txt(c,sub,155,y+53,12,WHITE,false,Paint.Align.LEFT);btn(c,b+" • "+cost,640,y+16,170,46,GREEN);}
    private void bagScreen(Canvas c){bg(c,Color.rgb(82,145,186),Color.rgb(48,88,124));header(c,"KHO ĐỒ");box(c,110,98,740,360,Color.argb(220,21,41,63),16);String[] it={"Cần câu cấp "+rod,"Nhân vật: "+new String[]{"Sở Tâm","Bá Thường","Lão Ngô"}[character],"Gỗ: "+wood,"Đá: "+stone,"Huy hiệu: "+gardenLevel};for(int i=0;i<it.length;i++){float x=150+(i%3)*220,y=145+(i/3)*125;box(c,x,y,190,90,Color.rgb(51,84,111),14);txt(c,"▣",x+95,y+35,23,GOLD,true,Paint.Align.CENTER);txt(c,it[i],x+95,y+66,14,WHITE,true,Paint.Align.CENTER);}btn(c,"ĐỔI NHÂN VẬT",385,395,190,45,PURPLE);}
    private void taskScreen(Canvas c){bg(c,Color.rgb(82,158,176),Color.rgb(66,105,142));header(c,"NHIỆM VỤ & THÀNH TÍCH");box(c,110,96,740,360,Color.argb(220,21,42,62),16);txt(c,"NHIỆM VỤ HẰNG NGÀY",145,134,19,WHITE,true,Paint.Align.LEFT);txt(c,"Hoàn thành "+dayFish+" lượt câu cá",150,190,17,WHITE,true,Paint.Align.LEFT);txt(c,"Phần thưởng: 100 xu",150,216,13,GOLD,false,Paint.Align.LEFT);btn(c,"NHẬN",685,165,120,48,GREEN);txt(c,"Thắng 1 màn Garden Defense",150,278,17,WHITE,true,Paint.Align.LEFT);txt(c,"Phần thưởng: 180 xu + XP",150,304,13,GOLD,false,Paint.Align.LEFT);txt(c,"Chơi 1 mini game",150,360,17,WHITE,true,Paint.Align.LEFT);txt(c,"Phần thưởng: 50 xu",150,386,13,GOLD,false,Paint.Align.LEFT);}
    private void settingsScreen(Canvas c){bg(c,Color.rgb(75,109,153),Color.rgb(42,59,92));header(c,"CÀI ĐẶT");box(c,180,110,600,300,Color.argb(220,20,39,61),18);txt(c,"CHAT LGBT GAME",480,165,26,WHITE,true,Paint.Align.CENTER);txt(c,"Phiên bản 1.0.0 • Offline • Lưu tiến trình trên máy",480,208,14,WHITE,false,Paint.Align.CENTER);btn(c,"LÀM MỚI THÔNG BÁO",350,250,260,46,BLUE);btn(c,"VỀ SẢNH",350,315,260,46,GREEN);}
    private void result(Canvas c,String title,String sub,boolean win){p.setColor(Color.argb(185,9,19,32));c.drawRect(0,0,W,H,p);box(c,260,170,440,210,Color.rgb(29,48,70),20);txt(c,title,480,220,25,win?GOLD:WHITE,true,Paint.Align.CENTER);txt(c,sub,480,258,15,WHITE,false,Paint.Align.CENTER);btn(c,"CHƠI LẠI",310,294,150,48,GREEN);btn(c,"VỀ SẢNH",500,294,150,48,BLUE);}

    @Override public boolean onTouchEvent(MotionEvent e){float x=(e.getX()-ox)/scale,y=(e.getY()-oy)/scale;if(e.getAction()==MotionEvent.ACTION_DOWN){touch(x,y);return true;}if(e.getAction()==MotionEvent.ACTION_UP||e.getAction()==MotionEvent.ACTION_CANCEL){reeling=false;return true;}return true;}
    private boolean hit(float x,float y,float a,float b,float w,float h){return x>=a&&x<=a+w&&y>=b&&y<=b+h;}
    private void touch(float x,float y){if(screen.equals("HUB")){if(hit(x,y,15,90,90,46))screen="SHOP";else if(hit(x,y,15,145,90,46))screen="BAG";else if(hit(x,y,15,200,90,46))screen="TASK";else if(hit(x,y,15,255,90,46))screen="SETTINGS";else if(hit(x,y,125,151,257,160))startGarden();else if(hit(x,y,394,151,257,160))startFish();else if(hit(x,y,663,151,282,160))startStudio();else if(hit(x,y,125,324,196,116))startTank();else if(hit(x,y,329,324,196,116))startHeist();else if(hit(x,y,533,324,196,116))startSurvival();else if(hit(x,y,737,324,208,116))startRace();else if(hit(x,y,756,465,170,43)){if(!giftClaimed){giftClaimed=true;coins+=100;energy=Math.min(100,energy+10);notice="Đã nhận quà đăng nhập +100 xu";persist();}else notice="Bạn đã nhận quà rồi";}else if(hit(x,y,125,454,820,65))screen="MINI";invalidate();return;}
        if(hit(x,y,14,12,50,43)){back();return;}
        if(screen.equals("GARDEN")){if(over){if(hit(x,y,300,285,170,65))startGarden();else if(hit(x,y,490,285,170,65))back();return;}if(hit(x,y,22,100,122,88))plantPick="SUN";else if(hit(x,y,22,202,122,88))plantPick="PEA";else if(hit(x,y,22,304,122,88))plantPick="NUT";else if(hit(x,y,22,410,122,38))paused=!paused;else if(x>=170&&x<746&&y>=111&&y<421){int col=(int)((x-170)/64),row=(int)((y-111)/62),cost=plantPick.equals("SUN")?50:plantPick.equals("PEA")?100:75;if(garden[row][col]==null&&sun>=cost){sun-=cost;garden[row][col]=new Plant(plantPick);}}}
        else if(screen.equals("FISH")){if(over){if(hit(x,y,300,285,170,65))startFish();else if(hit(x,y,490,285,170,65))back();return;}if(hit(x,y,750,405,175,52)&&!casting&&!hooked){casting=true;fishWait=0;}else if(hit(x,y,750,405,175,52)&&hooked){reeling=true;fishPct=Math.min(100,fishPct+10);}else if(hit(x,y,750,468,175,40)){character=(character+1)%3;persist();}}
        else if(screen.equals("STUDIO")){if(hit(x,y,755,12,105,43))studioRun=!studioRun;else if(hit(x,y,870,12,75,43)){StringBuilder b=new StringBuilder();for(String s:code){if(b.length()>0)b.append('\n');b.append(s);}save.edit().putString("script",b.toString()).apply();notice="Đã lưu kịch bản";}else if(hit(x,y,23,121,158,40))addCode("Di chuyển 10 bước");else if(hit(x,y,23,170,158,40))addCode("Xoay 15 độ");else if(hit(x,y,23,219,158,40))addCode("Nói Xin chào");else if(hit(x,y,23,268,158,40))addCode("Đợi 1 giây");else if(hit(x,y,23,317,158,40))addCode("Lặp lại 5 lần");else if(hit(x,y,23,366,158,40))addCode("Đổi màu");else if(hit(x,y,23,415,158,40))addCode("Tăng biến");else if(hit(x,y,230,443,142,42)&&code.size()>1)code.remove(code.size()-1);else if(hit(x,y,386,443,95,42))variable=Math.max(0,variable-1);else if(hit(x,y,492,443,95,42))variable++;}
        else if(screen.equals("TANK")){if(over){if(hit(x,y,300,285,170,65))startTank();else if(hit(x,y,490,285,170,65))back();return;}if(hit(x,y,55,450,80,58))heroX=Math.max(280,heroX-40);else if(hit(x,y,150,450,80,58))heroX=Math.min(720,heroX+40);else if(hit(x,y,810,444,110,64)){tankPts++;for(Iterator<Foe>i=foes.iterator();i.hasNext();){Foe f=i.next();if(Math.abs(f.x-heroX)<75&&f.y<heroY){i.remove();tankPts+=10;earn(3,2);break;}}}}
        else if(screen.equals("HEIST")){if(over){if(hit(x,y,300,285,170,65))startHeist();else if(hit(x,y,490,285,170,65))back();return;}heroX=Math.max(105,Math.min(865,x));heroY=Math.max(100,Math.min(490,y));if(Math.abs(heroX-230)<40&&Math.abs(heroY-170)<42)heistGems=Math.max(heistGems,1);if(Math.abs(heroX-410)<40&&Math.abs(heroY-300)<42)heistGems=Math.max(heistGems,2);if(Math.abs(heroX-590)<40&&Math.abs(heroY-170)<42)heistGems=3;if(heistGems>=3&&heroX>830&&heroY>420&&!over){over=true;heistWon=true;earn(150,30);}}
        else if(screen.equals("SURVIVAL")){if(hit(x,y,840,135,92,48)){wood+=2;}else if(hit(x,y,840,197,92,48)){stone+=2;}else if(hit(x,y,840,259,92,48)){buildMode=true;}else if(hit(x,y,840,321,92,48)){if(wood>=4){wood-=4;stone+=3;}}else if(x>=150&&x<822&&y>=115&&y<495){int col=(int)((x-150)/84),row=(int)((y-115)/76),i=row*8+col;if(buildMode&&!blocksPlaced[i]&&wood>=2&&stone>=1){wood-=2;stone--;blocksPlaced[i]=true;blocks++;buildMode=false;}else if(blocksPlaced[i]){blocksPlaced[i]=false;blocks--;wood++;}}}
        else if(screen.equals("RACE")){if(over){if(hit(x,y,300,285,170,65))startRace();else if(hit(x,y,490,285,170,65))back();return;}if(hit(x,y,60,448,100,58))raceLane=Math.max(0,raceLane-1);else if(hit(x,y,175,448,100,58))raceLane=Math.min(2,raceLane+1);}
        else if(screen.equals("MINI")){if(hit(x,y,155,245,150,78)||hit(x,y,375,245,150,78)||hit(x,y,595,245,150,78)||hit(x,y,815,245,120,78)||hit(x,y,155,360,150,78)||hit(x,y,375,360,150,78)||hit(x,y,595,360,150,78)||hit(x,y,815,360,120,78)){miniScore++;earn(2,1);}}
        else if(screen.equals("SHOP")){if(hit(x,y,640,166,170,46)){int cost=rod*250;if(coins>=cost){coins-=cost;rod++;persist();}else notice="Không đủ xu";}else if(hit(x,y,640,261,170,46)){if(coins>=150){coins-=150;energy=Math.min(100,energy+25);persist();}}else if(hit(x,y,640,356,170,46)){if(coins>=300){coins-=300;gems+=5;persist();}}}
        else if(screen.equals("BAG")){if(hit(x,y,385,395,190,45)){character=(character+1)%3;persist();}}
        else if(screen.equals("TASK")){if(hit(x,y,685,165,120,48)&&dayFish>0){dayFish=0;earn(100,10);}}
        else if(screen.equals("SETTINGS")){if(hit(x,y,350,315,260,46))back();}
        if(xp>=level*100){xp-=level*100;level++;gems+=2;}persist();invalidate();}
    private void addCode(String s){if(code.size()<8)code.add(s);else notice="Tối đa 8 khối trong bản này";}
    private void back(){screen="HUB";over=false;paused=false;studioRun=false;reeling=false;persist();invalidate();}

    public boolean backToHub(){
        if ("HUB".equals(screen)) return false;
        back();
        return true;
    }
}
