package com.example.animalinformation;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public class ImageLoadTask extends AsyncTask<Void,Void, Bitmap> {

        private String urlStr;
        private ImageView imageView;
        private HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();

        public ImageLoadTask(String urlStr, ImageView imageView) {
            this.urlStr = urlStr;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;
            try {
                if (bitmapHash.containsKey(urlStr)) {
                    Bitmap oldbitmap = bitmapHash.remove(urlStr);
                    if(oldbitmap != null) {
                        oldbitmap.recycle();
                        oldbitmap = null;
                    }
                }
                URL url = new URL(urlStr);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                bitmapHash.put(urlStr,bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        }
    }


    // 검색
    Button btnSearch, btnDetail, btnDate1, btnDate2;
    EditText editSearch, editDate1, editDate2;;
    GridLayout gridDetail;
    DatePickerDialog datePickerDialog;
    Button btnCity1, btnCity2, btnProtect, btnSpecies1, btnSpecies2, btnSex;



    //추가
    String msg, ma;
    // 동물 정보
    TextView textView0, textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9;
    // 동물 상세 정보 리스트
    ArrayList<String> listi = new ArrayList<>(10);
    ArrayList<String> listt = new ArrayList<>(10);


    // 검색정보
    // 위치 정보
    ArrayList<String> listCity1;    // 시도
    // 시도2
//    ArrayList<String> listSeoul;    // 서울 특별시
//    ArrayList<String> listBusan;
//    ArrayList<String> listDaegu;
//    ArrayList<String> listIncheon;
//    ArrayList<String> listGwangju;
//    ArrayList<String> listSejong;
//    ArrayList<String> listDaejeon;
//    ArrayList<String> listUlsan;
//    ArrayList<String> listGyeonggi;
//    ArrayList<String> listGangwon;
//    ArrayList<String> listChungbuk;
//    ArrayList<String> listChungnam;
//    ArrayList<String> listJeonbuk;
//    ArrayList<String> listJeonnam;
//    ArrayList<String> listGyeongbuk;
//    ArrayList<String> listGyeongNam;
//    ArrayList<String> listJeju;
    HashMap<String, ArrayList<String>> mapCity2 = new HashMap<String, ArrayList<String>>(); // 시군구 정리 해쉬맵으로 변경함.


    // 동물 정보
    ArrayList<String> listSpecies1;     // 축종
    // 축종2
    ArrayList<String> listSpecies2_dog;     // 개
    ArrayList<String> listSpecies2_cat;     // 고양이
    ArrayList<String> listSpecies2_etc;     // 기타
    ArrayList<String> listSex;      // 성별



    // 검색 url
    String city1Id = ""; //searchUprCd
    String city2Id = ""; //searchOrgCd
    String protectId = ""; //searchCareRegNo
    String species1Id = ""; //searchUpKindCd
    String species2Id = ""; //searchKindCd
    String sexId = ""; //searchSexCd
    String searchNumId = ""; //searchRfid
    String page = "1";

    String searchUrl = "https://www.animal.go.kr/front/awtis/public/publicList.do?totalCount=125&pageSize=10&boardId=&desertionNo=&menuNo=1000000055&searchSDate="
            + "2023-04-31" + "&searchEDate=" + "2023-05-31" + "&searchUprCd=" + city1Id + "&searchOrgCd=" + city2Id + "&searchCareRegNo=" + protectId +
            "&searchUpKindCd=" + species1Id + "&searchKindCd=" + species2Id + "&searchSexCd=" + sexId + "&searchRfid=" + searchNumId + "&&page=" + page;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        // 검색
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnDetail = (Button) findViewById(R.id.btnDetail);

        // 날짜
        btnDate1 = (Button) findViewById(R.id.btnDate1);
        btnDate2 = (Button) findViewById(R.id.btnDate2);
        editDate1 = (EditText) findViewById(R.id.editDate1);
        editDate2 = (EditText) findViewById(R.id.editDate2);

        // 상세 검색
        editSearch = (EditText) findViewById(R.id.editSearch);
        gridDetail = (GridLayout) findViewById(R.id.gridDetail);


        // 도시, 보호센터 정보
        btnCity1 = (Button) findViewById(R.id.btnCity1);
        // 짧게 클릭했을때 메뉴 뜨게
        setSearchBtnMenu(btnCity1); // 아래에서 함수로 연결함
        btnCity2 = (Button) findViewById(R.id.btnCity2);    // 마저 할 것 들...
        setSearchBtnMenu(btnCity2);
        btnProtect = (Button) findViewById(R.id.btnProtect);
        setSearchBtnMenu(btnProtect);

        // 동물 정보
        btnSpecies1 = (Button) findViewById(R.id.btnSpecies1);
        setSearchBtnMenu(btnSpecies1);
        btnSpecies2 = (Button) findViewById(R.id.btnSpecies2);
        setSearchBtnMenu(btnSpecies2);
        btnSex = (Button) findViewById(R.id.btnSex);
        setSearchBtnMenu(btnSex);


        //핸들러 사용을 위한 텍스트뷰 0~9 -동물 정보 입력
        textView0 = (TextView) findViewById(R.id.first0);
        textView1 = (TextView) findViewById(R.id.first1);
        textView2 = (TextView) findViewById(R.id.first2);
        textView3 = (TextView) findViewById(R.id.first3);
        textView4 = (TextView) findViewById(R.id.first4);
        textView5 = (TextView) findViewById(R.id.first5);
        textView6 = (TextView) findViewById(R.id.first6);
        textView7 = (TextView) findViewById(R.id.first7);
        textView8 = (TextView) findViewById(R.id.first8);
        textView9 = (TextView) findViewById(R.id.first9);

        final Bundle bundle = new Bundle();





        // 날짜 대입
        Calendar calendar = Calendar.getInstance();
        int year1 = calendar.get(Calendar.YEAR) - 1;
        int month1 = calendar.get(Calendar.MONTH) + 1;//월
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);//일

        String monStr1 = String.valueOf(month1).length() < 2 ? "0" + String.valueOf(month1) : String.valueOf(month1);
        String dayStr1 = String.valueOf(day1).length() < 2 ? "0" + String.valueOf(day1) : String.valueOf(day1);

        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH) + 1;//월
        int day2 = calendar.get(Calendar.DAY_OF_MONTH);//일

        String monStr2 = String.valueOf(month2).length() < 2 ? "0" + String.valueOf(month2) : String.valueOf(month2);
        String dayStr2 = String.valueOf(day2).length() < 2 ? "0" + String.valueOf(month2) : String.valueOf(day2);

        editDate1.setText(year1 + "-" + monStr1 + "-" + dayStr1);
        editDate2.setText(year2 + "-" + monStr2 + "-" + dayStr2);





        // 상세 검색 펼치기, 접기
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnDetail.getText().equals("접기")) {
                    gridDetail.setVisibility(View.GONE);
                    btnDetail.setText("상세 검색");
                } else if (btnDetail.getText().equals("상세 검색")) {
                    gridDetail.setVisibility(View.VISIBLE);
                    btnDetail.setText("접기");
                }
            }
        });



        // 날짜 팝업창
        btnDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String[] dateData = editDate1.getText().toString().split("-");
                    int year = Integer.parseInt(dateData[0]);
                    int month = Integer.parseInt(dateData[1]) - 1;
                    int day = Integer.parseInt(dateData[2]);

                    datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            month = month + 1;
                            String monStr = String.valueOf(month).length() < 2 ? "0" + String.valueOf(month) : String.valueOf(month);
                            String dayStr = String.valueOf(day).length() < 2 ? "0" + String.valueOf(day) : String.valueOf(day);
                            String date = year + "-" + monStr + "-" + dayStr;
                            editDate1.setText(date);
                        }
                    }, year, month, day);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Toast error = Toast.makeText(getApplicationContext(),"년월일을 \'-\'로 구분해주세요", Toast.LENGTH_SHORT);
                    error.show();
                }
            }

        });

        btnDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String[] dateData = editDate2.getText().toString().split("-");
                    int year2 = Integer.parseInt(dateData[0]);
                    int month2 = Integer.parseInt(dateData[1]) - 1;
                    int day2 = Integer.parseInt(dateData[2]);

                    datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            month = month + 1;
                            String monStr = String.valueOf(month).length() < 2 ? "0" + String.valueOf(month) : String.valueOf(month);
                            String dayStr = String.valueOf(day).length() < 2 ? "0" + String.valueOf(day) : String.valueOf(day);
                            String date = year + "-" + monStr + "-" + dayStr;
                            editDate2.setText(date);
                        }
                    }, year2, month2, day2);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Toast error = Toast.makeText(getApplicationContext(),"년월일을 \'-\'로 구분해주세요", Toast.LENGTH_SHORT);
                    error.show();
                }
            }
        });



        // 웹크롤링
        new Thread(){
            @Override
            public void run() {
                Document doc = null;
                try {
                    //페이지 넘기기는 url에 &&page=2를 붙이기
                    String url ="https://www.animal.go.kr/front/awtis/public/publicList.do?menuNo=1000000055";

                    doc = Jsoup.connect(url).get();

                    Document a = Jsoup.connect(url).get();

                    // 동물 정보
                    Elements anmalt = a.select(".boardList").select("li").select("dd");
                    Elements anmali = a.select(".boardlist").select(".thumbnail").select("img");


                    //정보 텍스트 가져오기
                    int sit = anmalt.size();
                    /*
                    for (int i=0; i<sit; i++){
                        if (i%10==1 || i%10==2 || i%10==3 || i%10==4 || i%10==5 || i%10==6) {
                            if (ma ==null) {
                                ma = anmalt.eq(i).text();
                        }
                            ma += anmalt.eq(i).text();
                            ma += "\n";
                            if(i%10==6){ ma+= "\n";}
                        }
                    }
                    */

                    for (int i=0; i<sit; i++){
                        if (i%10==1 || i%10==2 || i%10==3 || i%10==4 || i%10==5 || i%10==6) {
                            if (ma == null) {
                                ma = anmalt.eq(i).text();
                            }
                            ma += anmalt.eq(i).text();
                            if(i%10!=6) {ma += "\n";}
                            if (i % 10 == 6) {listt.add(ma); ma = "";}
                        }
                    }
                    for (int i=0; i<10; i++){
                        msg= listt.get(i);
                        bundle.putString("message"+i,msg);
                    }
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);


                    //사진 src 가져오기
                    String an;
                    int sii = anmali.size();
                    for (int i=0; i<sii; i++){
                        an = anmali.eq(i).attr("src");
                        listi.add(an);
                    }

                    //짧게 줄이는 법 찾는 중
                    ImageLoadTask task = new ImageLoadTask("https://www.animal.go.kr" + listi.get(0), (ImageView) findViewById(R.id.firsti0));
                    task.execute();
                    ImageLoadTask task2 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(1), (ImageView) findViewById(R.id.firsti1));
                    task2.execute();
                    ImageLoadTask task3 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(2), (ImageView) findViewById(R.id.firsti2));
                    task3.execute();
                    ImageLoadTask task4 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(3), (ImageView) findViewById(R.id.firsti3));
                    task4.execute();
                    ImageLoadTask task5 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(4), (ImageView) findViewById(R.id.firsti4));
                    task5.execute();
                    ImageLoadTask task6 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(5), (ImageView) findViewById(R.id.firsti5));
                    task6.execute();
                    ImageLoadTask task7 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(6), (ImageView) findViewById(R.id.firsti6));
                    task7.execute();
                    ImageLoadTask task8 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(7), (ImageView) findViewById(R.id.firsti7));
                    task8.execute();
                    ImageLoadTask task9 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(8), (ImageView) findViewById(R.id.firsti8));
                    task9.execute();
                    ImageLoadTask task10 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(9), (ImageView) findViewById(R.id.firsti9));
                    task10.execute();

                    /*
                    Elements contents = doc.select(".boardList").select(".txt");          //회차 id값 가져오기
                    Elements image_list = doc.select("boardList").select(".photo");

                    msg = contents.text();
                    bundle.putString("message", msg);
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    */


                    //------------------------------------------------------------
                    // 검색 메뉴 세팅
                    // 위치 정보
                    // 도시
                    Elements city = a.select("#searchUprCd").select("option");
                    listCity1 = new ArrayList<>(city.size());
                    for(int i = 0;  i < city.size(); i++) {
                        int start = city.eq(i).toString().indexOf(">") + 1;
                        int stop = city.eq(i).toString().lastIndexOf("<");
                        int start2 = city.eq(i).toString().indexOf('"') + 1;
                        int stop2 = city.eq(i).toString().lastIndexOf('"');
                        listCity1.add(city.eq(i).toString().substring(start2, stop2) + "," +city.eq(i).toString().substring(start, stop));
                    }

                    // 도시2
                    mapCity2.put(listCity1.get(1), listSetTextData(R.raw.seouldata));
                    mapCity2.put(listCity1.get(2), listSetTextData(R.raw.busandata));
                    mapCity2.put(listCity1.get(3), listSetTextData(R.raw.daegudata));
                    mapCity2.put(listCity1.get(4), listSetTextData(R.raw.incheondata));
                    mapCity2.put(listCity1.get(5), listSetTextData(R.raw.gwangjudata));
                    mapCity2.put(listCity1.get(6), listSetTextData(R.raw.sejongdata));
                    mapCity2.put(listCity1.get(7), listSetTextData(R.raw.daejeondata));
                    mapCity2.put(listCity1.get(8), listSetTextData(R.raw.ulsandata));
                    mapCity2.put(listCity1.get(9), listSetTextData(R.raw.gyeonggidata));
                    mapCity2.put(listCity1.get(10), listSetTextData(R.raw.gangwondata));
                    mapCity2.put(listCity1.get(11), listSetTextData(R.raw.chungbukdata));
                    mapCity2.put(listCity1.get(12), listSetTextData(R.raw.chungnamdata));
                    mapCity2.put(listCity1.get(13), listSetTextData(R.raw.jeonbukdata));
                    mapCity2.put(listCity1.get(14), listSetTextData(R.raw.jeonnamdata));
                    mapCity2.put(listCity1.get(15), listSetTextData(R.raw.gyeongbukdata));
                    mapCity2.put(listCity1.get(16), listSetTextData(R.raw.gyeongnamdata));
                    mapCity2.put(listCity1.get(17), listSetTextData(R.raw.jejudata));

//                    listSeoul = listSetTextData(R.raw.seouldata);    // 서울 특별시
//                    listBusan = listSetTextData(R.raw.busandata);
//                    listDaegu = listSetTextData(R.raw.daegudata);
//                    listIncheon = listSetTextData(R.raw.incheondata);
//                    listGwangju = listSetTextData(R.raw.gwangjudata);
//                    listSejong = listSetTextData(R.raw.sejongdata);
//                    listDaejeon = listSetTextData(R.raw.daejeondata);
//                    listUlsan = listSetTextData(R.raw.ulsandata);
//                    listGyeonggi = listSetTextData(R.raw.gyeonggidata);
//                    listGangwon = listSetTextData(R.raw.gangwondata);
//                    listChungbuk = listSetTextData(R.raw.chungbukdata);
//                    listChungnam = listSetTextData(R.raw.chungnamdata);
//                    listJeonbuk = listSetTextData(R.raw.jeonbukdata);
//                    listJeonnam = listSetTextData(R.raw.jeonnamdata);
//                    listGyeongbuk = listSetTextData(R.raw.gyeongbukdata);
//                    listGyeongNam = listSetTextData(R.raw.gyeongnamdata);
//                    listJeju = listSetTextData(R.raw.jejudata);


                    
                    
                    
                    //------------------------------------------------
                    // 동물 정보
                    //축종
                    Elements species1 = a.select("#searchUpKindCd").select("option");
                    listSpecies1 = new ArrayList<>(species1.size());
                    for(int i = 0;  i < species1.size(); i++) {
                        int start = species1.eq(i).toString().indexOf(">") + 1;
                        int stop = species1.eq(i).toString().lastIndexOf("<");
                        int start2 = species1.eq(i).toString().indexOf('"') + 1;
                        int stop2 = species1.eq(i).toString().lastIndexOf('"');
                        listSpecies1.add(species1.eq(i).toString().substring(start2, stop2) + "," + species1.eq(i).toString().substring(start, stop));
                    }

                    //축종2
                    listSpecies2_dog =  listSetTextData(R.raw.dogdata);
                    listSpecies2_cat = listSetTextData(R.raw.catdata);

                    listSpecies2_etc = new ArrayList<>(2);
                    listSpecies2_etc.add(",전체"); listSpecies2_etc.add("000117,기타 축종");


                    // 성별
                    Elements sex = a.select("#searchSexCd").select("option");
                    listSex = new ArrayList<>(sex.size());
                    for(int i = 0;  i < sex.size(); i++) {
                        int start = sex.eq(i).toString().indexOf(">") + 1;
                        int stop = sex.eq(i).toString().lastIndexOf("<");
                        int start2 = sex.eq(i).toString().indexOf('"') + 1;
                        int stop2 = sex.eq(i).toString().lastIndexOf('"');
                        listSex.add(sex.eq(i).toString().substring(start2, stop2) + "," + sex.eq(i).toString().substring(start, stop));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        
        
        // ------------------------------------------------------
        // 검색 새로고침
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(editDate1.getText().toString().split("-").length - 1 == 2) || !(editDate2.getText().toString().split("-").length - 1 == 2)) {
                    Toast error = Toast.makeText(getApplicationContext(),"년월일을 \'-\' 2개로 구분해주세요", Toast.LENGTH_SHORT);
                    error.show();
                } else {
                    searchUrl = "https://www.animal.go.kr/front/awtis/public/publicList.do?totalCount=125&pageSize=10&boardId=&desertionNo=&menuNo=1000000055&searchSDate="
                            + editDate1.getText().toString() + "&searchEDate=" + editDate2.getText().toString() + "&searchUprCd=" + city1Id + "&searchOrgCd=" + city2Id + "&searchCareRegNo=" + protectId +
                            "&searchUpKindCd=" + species1Id + "&searchKindCd=" + species2Id + "&searchSexCd=" + sexId + "&searchRfid=" + searchNumId + "&&page=" + page;
                    //Log.v("url",searchUrl);

                    //클롤링 값 초기화
                    msg = null;
                    ma = null;
                    listi.clear();
                    listt.clear();
                    bundle.clear();



                    // 웹크롤링         // 현재 웹페이지에 있는 동물 데이터가 10개가 아니라면 오류가 남.
                    new Thread(){
                        @Override
                        public void run() {
                            Document doc = null;
                            try {
                                //페이지 넘기기는 url에 &&page=2를 붙이기
                                String url =searchUrl;

                                doc = Jsoup.connect(url).get();

                                Document a = Jsoup.connect(url).get();

                                // 동물 정보
                                Elements anmalt = a.select(".boardList").select("li").select("dd");
                                Elements anmali = a.select(".boardlist").select(".thumbnail").select("img");

                                // 웹크롤링 확인용
//                                Log.v("log","hi");
//                                Log.v("log",anmalt.toString());

                                //정보 텍스트 가져오기
                                int sit = anmalt.size();
                    /*
                    for (int i=0; i<sit; i++){
                        if (i%10==1 || i%10==2 || i%10==3 || i%10==4 || i%10==5 || i%10==6) {
                            if (ma ==null) {
                                ma = anmalt.eq(i).text();
                        }
                            ma += anmalt.eq(i).text();
                            ma += "\n";
                            if(i%10==6){ ma+= "\n";}
                        }
                    }
                    */

                                for (int i=0; i<sit; i++){
                                    if (i%10==1 || i%10==2 || i%10==3 || i%10==4 || i%10==5 || i%10==6) {
                                        if (ma == null) {
                                            ma = anmalt.eq(i).text();
                                        }
                                        ma += anmalt.eq(i).text();
                                        if(i%10!=6) {ma += "\n";}
                                        if (i % 10 == 6) {listt.add(ma); ma = "";}
                                    }
                                }
                                for (int i=0; i<10; i++){
                                    msg= listt.get(i);
                                    bundle.putString("message"+i,msg);
                                }
                                Message msg = handler.obtainMessage();
                                msg.setData(bundle);
                                handler.sendMessage(msg);






                                //사진 src 가져오기
                                String an;
                                int sii = anmali.size();
                                for (int i=0; i<sii; i++){
                                    an = anmali.eq(i).attr("src");
                                    listi.add(an);
                                }

                                //짧게 줄이는 법 찾는 중
                                ImageLoadTask task = new ImageLoadTask("https://www.animal.go.kr" + listi.get(0), (ImageView) findViewById(R.id.firsti0));
                                task.execute();
                                ImageLoadTask task2 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(1), (ImageView) findViewById(R.id.firsti1));
                                task2.execute();
                                ImageLoadTask task3 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(2), (ImageView) findViewById(R.id.firsti2));
                                task3.execute();
                                ImageLoadTask task4 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(3), (ImageView) findViewById(R.id.firsti3));
                                task4.execute();
                                ImageLoadTask task5 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(4), (ImageView) findViewById(R.id.firsti4));
                                task5.execute();
                                ImageLoadTask task6 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(5), (ImageView) findViewById(R.id.firsti5));
                                task6.execute();
                                ImageLoadTask task7 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(6), (ImageView) findViewById(R.id.firsti6));
                                task7.execute();
                                ImageLoadTask task8 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(7), (ImageView) findViewById(R.id.firsti7));
                                task8.execute();
                                ImageLoadTask task9 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(8), (ImageView) findViewById(R.id.firsti8));
                                task9.execute();
                                ImageLoadTask task10 = new ImageLoadTask("https://www.animal.go.kr" + listi.get(9), (ImageView) findViewById(R.id.firsti9));
                                task10.execute();

                                //Log.v("오류없이 실행되는지 확인용","dd");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            //텍스트 넣기
            textView0.setText(bundle.getString("message0"));
            textView1.setText(bundle.getString("message1"));
            textView2.setText(bundle.getString("message2"));
            textView3.setText(bundle.getString("message3"));
            textView4.setText(bundle.getString("message4"));
            textView5.setText(bundle.getString("message5"));
            textView6.setText(bundle.getString("message6"));
            textView7.setText(bundle.getString("message7"));
            textView8.setText(bundle.getString("message8"));
            textView9.setText(bundle.getString("message9"));
        }
    };


    // 컨텍스트 메뉴
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == btnCity1){
            menu.setHeaderTitle("도시 선택");
            for(int i = 0;  i < listCity1.size(); i++) {
                menu.add(0, i, 0, listCity1.get(i).substring(listCity1.get(i).indexOf(",")+1));
            }
        } else if (v == btnCity2) {
            menu.setHeaderTitle("시군구 선택");
            if (btnCity1.getText().equals("전체")) {
                menu.add(1,0,0, "전체");
            } else {
                for(int i = 1; i < mapCity2.size() + 1; i++) {
                    if (btnCity1.getText().equals(listCity1.get(i).substring(listCity1.get(i).indexOf(",")+1))){
                        for (int j = 0; j < mapCity2.get(listCity1.get(i)).size(); j++) {
                            menu.add(1,j,0, mapCity2.get(listCity1.get(i)).get(j).substring(mapCity2.get(listCity1.get(i)).get(j).indexOf(",")+1));
                        }
                    }
                }
            }


//            else if (btnCity1.getText().equals("서울특별시 ")) {
//                for(int i = 0;  i < listSeoul.size(); i++) {
//                    menu.add(1, i, 0, listSeoul.get(i));
//                }
//            } else if (btnCity1.getText().equals("부산광역시 ")) {
//                for(int i = 0;  i < listBusan.size(); i++) {
//                    menu.add(1, i, 0, listBusan.get(i));
//                }
//            } else if (btnCity1.getText().equals("인천광역시 ")) {
//                for(int i = 0;  i < listIncheon.size(); i++) {
//                    menu.add(1, i, 0, listIncheon.get(i));
//                }
//            } else if (btnCity1.getText().equals("광주광역시 ")) {
//                for(int i = 0;  i < listGwangju.size(); i++) {
//                    menu.add(1, i, 0, listGwangju.get(i));
//                }
//            } else if (btnCity1.getText().equals("세종특별자치시 ")) {
//                for(int i = 0;  i < listSejong.size(); i++) {
//                    menu.add(1, i, 0, listSejong.get(i));
//                }
//            } else if (btnCity1.getText().equals("대전광역시 ")) {
//                for(int i = 0;  i < listDaejeon.size(); i++) {
//                    menu.add(1, i, 0, listDaejeon.get(i));
//                }
//            } else if (btnCity1.getText().equals("울산광역시 ")) {
//                for(int i = 0;  i < listUlsan.size(); i++) {
//                    menu.add(1, i, 0, listUlsan.get(i));
//                }
//            } else if (btnCity1.getText().equals("경기도 ")) {
//                for(int i = 0;  i < listGyeonggi.size(); i++) {
//                    menu.add(1, i, 0, listGyeonggi.get(i));
//                }
//            } else if (btnCity1.getText().equals("강원도 ")) {
//                for(int i = 0;  i < listGangwon.size(); i++) {
//                    menu.add(1, i, 0, listGangwon.get(i));
//                }
//            } else if (btnCity1.getText().equals("충청북도 ")) {
//                for(int i = 0;  i < listChungbuk.size(); i++) {
//                    menu.add(1, i, 0, listChungbuk.get(i));
//                }
//            } else if (btnCity1.getText().equals("충청남도 ")) {
//                for(int i = 0;  i < listChungnam.size(); i++) {
//                    menu.add(1, i, 0, listChungnam.get(i));
//                }
//            } else if (btnCity1.getText().equals("전라북도 ")) {
//                for(int i = 0;  i < listJeonbuk.size(); i++) {
//                    menu.add(1, i, 0, listJeonbuk.get(i));
//                }
//            } else if (btnCity1.getText().equals("전라남도 ")) {
//                for(int i = 0;  i < listJeonnam.size(); i++) {
//                    menu.add(1, i, 0, listJeonnam.get(i));
//                }
//            } else if (btnCity1.getText().equals("경상북도 ")) {
//                for(int i = 0;  i < listGyeongbuk.size(); i++) {
//                    menu.add(1, i, 0, listGyeongbuk.get(i));
//                }
//            } else if (btnCity1.getText().equals("경상남도 ")) {
//                for(int i = 0;  i < listGyeongNam.size(); i++) {
//                    menu.add(1, i, 0, listGyeongNam.get(i));
//                }
//            } else if (btnCity1.getText().equals("제주특별차치도 ")) {
//                for(int i = 0;  i < listJeju.size(); i++) {
//                    menu.add(1, i, 0, listJeju.get(i));
//                }
//            }
        } else if (v == btnSpecies1) {
            menu.setHeaderTitle("동물 선택");
            for(int i = 0;  i < listSpecies1.size(); i++) {
                menu.add(3, i, 0, listSpecies1.get(i).substring(listSpecies1.get(i).indexOf(",")+1));
            }
        } else if (v == btnSpecies2) {
            menu.setHeaderTitle("종 선택");
            if (btnSpecies1.getText().equals("전체")) {
                menu.add(4,0,0, "전체");
            } else if (btnSpecies1.getText().equals("개")) {
                for(int i = 0;  i < listSpecies2_dog.size(); i++) {
                    menu.add(4, i, 0, listSpecies2_dog.get(i).substring(listSpecies2_dog.get(i).indexOf(",")+1));
                }
            } else if (btnSpecies1.getText().equals("고양이")) {
                for(int i = 0;  i < listSpecies2_cat.size(); i++) {
                    menu.add(4, i, 0, listSpecies2_cat.get(i).substring(listSpecies2_cat.get(i).indexOf(",")+1));
                }
            } else if (btnSpecies1.getText().equals("기타")) {
                for(int i = 0;  i < listSpecies2_etc.size(); i++) {
                    menu.add(4, i, 0, listSpecies2_etc.get(i).substring(listSpecies2_etc.get(i).indexOf(",")+1));
                }
            } 
        } else if (v == btnSex) {
            menu.setHeaderTitle("성별 선택");
            for(int i = 0;  i < listSex.size(); i++) {
                menu.add(5, i, 0, listSex.get(i).substring(listSex.get(i).indexOf(",")+1));
            }
        }
    }

    // 컨텍스트 메뉴 선택시 일어나는 일
    public boolean onContextItemSelected (MenuItem item) {
        switch(item.getGroupId()) {
            case 0:         // 시도
                if (0 <= item.getItemId() && item.getItemId() < listCity1.size()) {
                    btnCity1.setText(listCity1.get(item.getItemId()).substring(listCity1.get(item.getItemId()).indexOf(",")+1));
                    btnCity2.setText("전체");
                    city1Id = listCity1.get(item.getItemId()).substring(0 ,listCity1.get(item.getItemId()).indexOf(","));
                    city2Id = "";
                    // 나중에 부속 메뉴들 "전체"로 초기화 하는 코드 넣기
                    return true;
                } return false;
            case 1:         // 시군구
                if (btnCity1.getText().equals("전체")) {
                    if (0 <= item.getItemId() && item.getItemId() < 1) {
                        btnCity2.setText("전체");
                        city2Id = "";
                        return true;
                    } return false;
                } else {
                    for(int i = 1; i < mapCity2.size() + 1; i++) {
                        if (btnCity1.getText().equals(listCity1.get(i).substring(listCity1.get(i).indexOf(",")+1))){
                            if (0 <= item.getItemId() && item.getItemId() < mapCity2.get(listCity1.get(i)).size()) {
                                btnCity2.setText(mapCity2.get(listCity1.get(i)).get(item.getItemId()).substring(mapCity2.get(listCity1.get(i)).get(item.getItemId()).indexOf(",")+1));
                                city2Id = mapCity2.get(listCity1.get(i)).get(item.getItemId()).substring(0,mapCity2.get(listCity1.get(i)).get(item.getItemId()).indexOf(","));
                                return true;
                            } return false;
                        }
                    }
                }

//                else if (btnCity1.getText().equals("서울특별시 ")) {
//                    if (0 <= item.getItemId() && item.getItemId() < listSeoul.size()) {
//                        btnCity2.setText(listSeoul.get(item.getItemId()));
//                        return true;
//                    } return false;
//                } else if (btnCity1.getText().equals("부산광역시 ")) {
//                    if (0 <= item.getItemId() && item.getItemId() < listBusan.size()) {
//                        btnCity2.setText(listBusan.get(item.getItemId()));
//                        return true;
//                    } return false;
//                } else if (btnCity1.getText().equals("대구광역시 ")) {
//                    if (0 <= item.getItemId() && item.getItemId() < listDaegu.size()) {
//                        btnCity2.setText(listDaegu.get(item.getItemId()));
//                        return true;
//                    } return false;
//                }
                
                
            case 3:         // 동물
                if (0 <= item.getItemId() && item.getItemId() < listSpecies1.size()) {
                    btnSpecies1.setText(listSpecies1.get(item.getItemId()).substring(listSpecies1.get(item.getItemId()).indexOf(",")+1));
                    btnSpecies2.setText("전체");
                    species1Id = listSpecies1.get(item.getItemId()).substring(0, listSpecies1.get(item.getItemId()).indexOf(","));
                    species2Id = "";
                    return true;
                } return false;
            case 4:         // 종선택
                if (btnSpecies1.getText().equals("전체")) {
                    if (0 <= item.getItemId() && item.getItemId() < 1) {
                        btnSpecies2.setText("전체");
                        species2Id = "";
                        return true;
                    } return false;
                } else if (btnSpecies1.getText().equals("개")) {
                    if (0 <= item.getItemId() && item.getItemId() < listSpecies2_dog.size()) {
                        btnSpecies2.setText(listSpecies2_dog.get(item.getItemId()).substring(listSpecies2_dog.get(item.getItemId()).indexOf(",")+1));
                        species2Id = listSpecies2_dog.get(item.getItemId()).substring(0, listSpecies2_dog.get(item.getItemId()).indexOf(","));
                        return true;
                    } return false;
                } else if (btnSpecies1.getText().equals("고양이")) {
                    if (0 <= item.getItemId() && item.getItemId() < listSpecies2_cat.size()) {
                        btnSpecies2.setText(listSpecies2_cat.get(item.getItemId()).substring(listSpecies2_cat.get(item.getItemId()).indexOf(",")+1));
                        species2Id = listSpecies2_cat.get(item.getItemId()).substring(0, listSpecies2_cat.get(item.getItemId()).indexOf(","));
                        return true;
                    } return false;
                } else if (btnSpecies1.getText().equals("기타")) {
                    if (0 <= item.getItemId() && item.getItemId() < listSpecies2_etc.size()) {
                        btnSpecies2.setText(listSpecies2_etc.get(item.getItemId()).substring(listSpecies2_etc.get(item.getItemId()).indexOf(",") + 1));
                        species2Id = listSpecies2_etc.get(item.getItemId()).substring(0, listSpecies2_etc.get(item.getItemId()).indexOf(","));
                        return true;
                    } return false;
                }
            case 5:         // 성별 선택
                if (0 <= item.getItemId() && item.getItemId() < listSex.size()) {
                    btnSex.setText(listSex.get(item.getItemId()).substring(listSex.get(item.getItemId()).indexOf(",")+1));
                    sexId = listSex.get(item.getItemId()).substring(0, listSex.get(item.getItemId()).indexOf(","));
                    return true;
                } return false;
        }
        return false;
    }

    // 컨텍스트 메뉴 함수
    // 버튼과 메뉴 연결 (짧은 터치)
    public void setSearchBtnMenu(Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForContextMenu(btn);
                openContextMenu(btn);
                unregisterForContextMenu(btn);
            }
        });
    }


    // 데이터 리스트 추가 함수
    public void listAddData(ArrayList<String> list, String[] data) {
        for (int i = 0; i < data.length; i++) {
            int start = data[i].indexOf(">") + 1;
            int stop = data[i].lastIndexOf("<");
            int start2 = data[i].indexOf('"') + 1;
            int stop2 = data[i].lastIndexOf('"');
            list.add(data[i].substring(start2, stop2) + "," + data[i].substring(start, stop));
        }
    }

    // 텍스트 데이터 한번에 추가
    public ArrayList<String> listSetTextData(int R) throws IOException {
        InputStream in = getResources().openRawResource(R);
        byte[] inByte = new byte[in.available()];
        in.read(inByte);
        String data = new String(inByte, "utf-8");
        String[] dataList = data.split("><");

        ArrayList<String> list = new ArrayList<String>(dataList.length);
        listAddData(list, dataList);
        return list;
    }

}