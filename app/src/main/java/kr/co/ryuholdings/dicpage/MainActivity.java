package kr.co.ryuholdings.dicpage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity
{


    public static final String tableName = "DIC";
    DataBaseHelper helper;
    SQLiteDatabase database;

    LinearLayout ll_dia;
    HorizontalScrollView hsv;

    String arrayWord[] = new String[2000];
    String arrayDefi[] = new String[2000];


    TextView str_defi;


    SpannableStringBuilder ssb;

    int dbCount;
    int count;

    int start_char = 0;
    int len_char = 0;
    int end_char = 0;
    int interval = 0;

    ScrollView scv;
    String sentence =
            "For home theater set up, service must be purchased by 1/28/06. Excludes all custom installation including wall-penetrating workd.\n\n\n\n\nFor home theater set up, service must be purchased by 1/28/06. Excludes all custom installation including wall-penetrating workd.\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\nFor home theater set up, service must be purchased by 1/28/06. Excludes all custom installation including wall-penetrating workd.\n"+
                    "\n" +
                    "\n" +
                    "\n";

    String reSentence = " "+sentence+" ";


    TextView tv;
    TextView str_word;

    String mSentence = sentence.replaceAll("\\s", "");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);

        scv = (ScrollView) findViewById(R.id.scv);
        int version = 1;
        String name = "dic.db";

        helper = new DataBaseHelper(getApplicationContext(), name, null, version);
        database = helper.getWritableDatabase();
        String sql = "select word,definition from " + tableName;
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor != null)
        {
            count = cursor.getCount();
            for (int i = 0; i < count; i++)
            {
                cursor.moveToNext();
                String word = cursor.getString(0);
                arrayWord[i] = word;
                String defi = cursor.getString(1);
                arrayDefi[i] = defi;
            }
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(addClickable(sentence), TextView.BufferType.SPANNABLE);
        scv.fullScroll(View.FOCUS_DOWN);

    }



    public SpannableStringBuilder addClickable(String str)
    {
        ssb = new SpannableStringBuilder(str);
        for (dbCount = 0; dbCount < count; dbCount++)
        {
            interval = 0;
            start_char = 0;
            len_char = 0;
            end_char = 0;
            while(true)
            {
                if (mSentence.matches("(.*)"+arrayWord[dbCount]+"(.*)"))
                {
                    start_char = reSentence.indexOf(arrayWord[dbCount], interval);
                    if (start_char == -1) break;
                    len_char = arrayWord[dbCount].length();
                    end_char = start_char + len_char;
                    final String ClickString = reSentence.substring(start_char, end_char);
                    final int s = dbCount;
                    if ((reSentence.charAt(end_char) == ' ' || reSentence.charAt(end_char) == '.' || reSentence.charAt(end_char) == '-'|| reSentence.charAt(end_char) == ',' ||reSentence.charAt(end_char) == '\n'))
                    {
                        if (reSentence.charAt(start_char - 1) == ' ' || reSentence.charAt(start_char - 1) == '\n' || reSentence.charAt(start_char - 1) == '-')
                        {
                            ssb.setSpan(new ClickableSpan()
                            {
                                @Override
                                public void onClick(View widget)
                                {
                                    LayoutInflater dialog = LayoutInflater.from(MainActivity.this);
                                    final View dialogLayout = dialog.inflate(R.layout.test_dialog, null);
                                    final Dialog dialogDic = new Dialog(MainActivity.this);
                                    str_word = (TextView) dialogLayout.findViewById(R.id.str_word);
                                    str_defi = (TextView) dialogLayout.findViewById(R.id.str_defi);
                                    ll_dia = (LinearLayout) dialogLayout.findViewById(R.id.ll_dia);
                                    hsv = (HorizontalScrollView) dialogLayout.findViewById(R.id.hsv);
                                    try
                                    {
                                        str_word.setText("" + ClickString);
                                        str_defi.setText("" + arrayDefi[s].toString());
                                    } catch (Exception e)
                                    {
                                        e.getMessage();
                                    }


                                    dialogDic.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialogDic.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialogDic.setContentView(dialogLayout);

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    Window window = dialogDic.getWindow();
                                    lp.copyFrom(window.getAttributes());
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                    window.setAttributes(lp);

                                    dialogDic.show();

                                    ll_dia.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogDic.cancel();
                                        }
                                    });

                                    str_defi.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogDic.cancel();
                                        }
                                    });

                                    hsv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogDic.cancel();
                                        }
                                    });

                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    ds.setUnderlineText(false);
                                }
                            }, start_char, end_char, 0);
                            ssb.setSpan(new ForegroundColorSpan(Color.BLACK), start_char, end_char, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            interval = start_char + len_char;
                        } else
                        {
                            interval = start_char + len_char;
                        }
                    }
                    else
                    {
                        interval = start_char + len_char;
                    }
                }
                else
                {
                    break;
                }
                if(start_char ==-1) break;
            }
        }

        return ssb;
    }




    class DataBaseHelper extends SQLiteOpenHelper
    {

        public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase database)
        {
            createTable(database);
            insertData(database);
        }

        @Override
        public void onOpen(SQLiteDatabase database)
        {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }

        private void createTable(SQLiteDatabase database)
        {
            String sql = "create table " + tableName + "(word text,definition text)";
            try
            {
                database.execSQL(sql);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        private void insertData(SQLiteDatabase database)
        {
            database.beginTransaction();
            try
            {
                String sql = "insert into " + tableName + "(word,definition)" + " values('For','1.(...가 갖게 하기) 위한, ...의, ...에 둘\n   There is start_char letter for you\n   당신 앞으로 편지가 한 통 왔어요.\n2.(...을 돕기) 위해\n   What can I do for you?\n   뭘 도와 드릴까요?')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('asdfa','몰라')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('home','1.집\n   We are not far from my home now.\n   이제 우리가 저의 집에 거의 다 왔어요.\n   Old people prefer to stay in their own homes.\n   노인들은 자기 집에 게속 있기를 더 원한다.\n2.(사고 팔 수 있는 재산으로서의)주택\n   start_char holiday/summer home\n   휴가용 휴양용 주택')";
                database.execSQL(sql);


                sql = "insert into " + tableName + "(word,definition)" + " values('theater','1.극장\n   start_char movie theater\n   영화관\n2.(연극 영화의)관객\n   The theater wept.\n')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('set','1.(특정한 장소에)놓다\n   She set start_char tray down on the table.\n   그녀가 쟁반을 탁자 위에 내려놓았다.\n   They ate everything thar was set in front of them.\n 그들은 자기들 앞에 놓인 것을 전부 다 먹었다.')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('service','(공공) 서비스사업\n   the ambulance/bus/telephone, dtc. service')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('up','<방향 이동> 위치가 위쪽을 향하거나 위쪽에 있음을 나타낼 때 씀\n   I pinned the notice up on the wall.\n   나는 그 안내문을 벽에 (핀으로) 붙였다.\n   Lay the cards face up on the table.\n   그 카드들을 앞면이 위쪽에 오도록 탁자 위에 놓아라.')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('must','1.(필요성 중요성을 나타내어)...해야 하다\n   I must admit I was surprised it cost so little.\n   Must you always question everything I say?')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('be','1. 있다, 존재하다\n   Is there start_char God?\n   Once upon start_char time there was start_char princess...\n   옛날 옛적에 한 공주가 있었습니다...\n   I tried phoning but there was no answer.\n   내가 전화를 해 봤지만 받는 사람이 없었다.')";
                database.execSQL(sql);


                //여기서부터
                sql = "insert into " + tableName + "(word,definition)" + " values('purchased','1.구입, 구매, 매입\n   to make start_char purchase\n   구매를 하다\n   Keep your receipt as proof of purchase.\n   구입 증거물로 영수증을 보관하라.\n   The company has just announced its 27 million purchase of Park Hotel.\n   그 회사는 얼마 전에 파크 호텔을 2700만 파운드에 매입했다고 발표했다.')";
                database.execSQL(sql);


                sql = "insert into " + tableName + "(word,definition)" + " values('by','1. ...옆[가]에\n   start_char house by the river\n   강가에 있는 집\n   The telephone is by the window\n   전화는 창가에 있다.\n   Come and sit by me.\n   내 옆에 와서 앉아.')";
                database.execSQL(sql);


                sql = "insert into " + tableName + "(word,definition)" + " values('Excludes','1.제외[배제]하다\n   The cost of borrowing has been excluded from the inflation figures.\n   차용 비용은 그 인플레이션 수치에서 제외되었다.\n   Try excluding fat from your diet.\n   늘 먹는 음식에서 지방을 배제하도록 하라.\n   Buses run every hour, Sundays excluded.\n   일요일을 제외하고는 버스가 매 시간 운행한다.')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('all','1.모든\n   All hourses are animals, but not all animals are horses.\n   모든 말은 동물이다. 하지만 모든 동물이 말은 아니다.\n   Cars were coming from all directions.\n   사방에서 차들이 오고 있었다.\n   All the people you invited are coming.\n   당신이 초대한 사람들이 다 올 것이다.')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('custom','1.관습, 풍습\n   an old/ancient custom\n   오래된/아주 오래된 관습\n   the custom of giving presents at Christmas\n   크리스마스에 선물을 주는 풍습')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('installation','1.설치\n   installation costs\n   설치비\n   Installation of the new system will take several days.\n   새 시스템을 설치하는 데는 여러 날이 걸릴 것이다.')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('including','1....을 포함하여\n   I have got three days holiday including New Years Day.\n   나는 1월 1일을 포함하여 사흘간 휴가이다.\n   Six people were killed in the riot, including start_char policeman.\n   그 폭동에서 경찰관 한 명을 포함하여 6명이 사망했다.')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('wall','1.담\n   The fields were divided by stone walls.\n   들판들은 돌담으로 나뉘어져 있었다.\n   He sat on the wall and watched the others playing.\n  그는 담 위에 앉아서 다른 사람들이 노는 것을 지켜보았다.')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('penetrating','1.눈 시선등이 마음속을 꿰뚫어 보는 듯한\n   penetrating blue eyes\n   마음속을 꿰뚫어 보는 듯한 푸른색 눈\n   start_char penetrating gaze/look/stare\n   마음속을 꿰둟어 보는 듯한 응시/시선/응시')";
                database.execSQL(sql);

                sql = "insert into " + tableName + "(word,definition)" + " values('workd','1.일하다, 작업근무하다\n   I can not work if i am cold.\n   나는 추우면 일을 하지 못한다.\n   i have been working at my assignment all day.\n   나는 하루 종일 내게 맡겨진 일을 하고 있는 중이다.\n   He is working on start_char new novel.\n   그는 새 소설을 집필하고 있다.')";
                database.execSQL(sql);

                database.setTransactionSuccessful();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                database.endTransaction();
            }
        }
    }
}
