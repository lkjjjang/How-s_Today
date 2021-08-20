package com.example.hows_today;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FutureWeatherListAdapter extends RecyclerView.Adapter<FutureWeatherListAdapter.CustomViewHolder> {

    private final ArrayList<Weather> weathers;
    private final int rainProbability = 35;

    public FutureWeatherListAdapter(ArrayList<Weather> weathers) {
        this.weathers = weathers;
    }

    @NonNull
    @Override
    public FutureWeatherListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_column, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FutureWeatherListAdapter.CustomViewHolder holder, int position) {
        FutureWeather futureWeather = (FutureWeather) this.weathers.get(position);

        holder.tv_futureTime.setText(transFormDate(futureWeather.getDate()));
        holder.tv_futureRain.setText(transFormRainProbability(futureWeather.getRainProbability()));
        holder.tv_futureTemp.setText(futureWeather.getTemperature());
        holder.iv_futureImg.setImageResource(getImage(futureWeather));
    }

    @Override
    public int getItemCount() {
        return this.weathers.size();
    }

    private int getImage(FutureWeather futureWeather) {
        int length = futureWeather.getDate().length();
        String date = futureWeather.getDate().substring(length - 4, length - 2);

        int time = Integer.parseInt(date);
        int rainProbability = Integer.parseInt(futureWeather.getRainProbability());
        int sky = Integer.parseInt(futureWeather.getSkyCondition());

        int result = 0;

        if (time >= 6 && time <= 18) {
            if (rainProbability >= this.rainProbability) {
                result = R.drawable.rain;
            } else {
                if (sky <= 5) {
                    result = R.drawable.sun;
                } else if (sky <= 8) {
                    result = R.drawable.sun_cloudy;
                } else {
                    result = R.drawable.cloudy;
                }
            }
        } else {
            if (rainProbability >= this.rainProbability) {
                result = R.drawable.rain;
            } else {
                result = R.drawable.half_moon;
            }
        }

        return result;
    }

    private String transFormRainProbability(String rain) {
        int resultInt = Integer.parseInt(rain);

        if (resultInt <= this.rainProbability) {
            return "";
        }

        return Integer.toString(resultInt) + "%";
    }

    private String transFormDate(String date) {
        int length = date.length();
        String dateStr = date.substring(length - 4, length - 2); // 끝 두자리는 00 임

        if (dateStr.equals("24")) {
            dateStr = "00";
        }

        return dateStr + "시";
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv_futureImg;
        protected TextView tv_futureTime;
        protected TextView tv_futureRain;
        protected TextView tv_futureTemp;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_futureImg = (ImageView) itemView.findViewById(R.id.iv_futureImg);
            this.tv_futureTime = (TextView) itemView.findViewById(R.id.tv_futureTime);
            this.tv_futureRain = (TextView) itemView.findViewById(R.id.tv_futureRain);
            this.tv_futureTemp = (TextView) itemView.findViewById(R.id.tv_futureTemp);
        }
    }
}

/*//이 커스텀 Adapter 클래스 설게에서 가장 중요한 메소드로서
 //ListView에서 한 항목의 View 모양과 값을 설정하는 메소드
 //AdapterView의 일종인 ListView는 설정된 Adapter(이 예제에서는 MemberDataAdapter)에게
 //대량의 데이터들(datas : MemberData객체 배열)을 보여주기에 적절한 View로 만들고
 //해당 데이터의 값으로 만들어 내는 핵심 메소드로서 ListView를 위에 만든 getCount()메소드의 리턴값만큼
 //getView를 요구하여  목록에 나열함.
 //즉, 이 메소드의 리턴값인 View 가 ListView의 한 항목을 의미합니다.
 //우리는 이 리턴될 View의 모양을 res폴더>>layout폴더>>list_row.xml 파일을 만들어 설계합니다.
 //첫번째 파라미터 position : ListView에 놓여질 목록의 위치.
 //두번째 파라미터 convertview : 리턴될 View로서 List의 한 함목의 View 객체(자세한 건 아래에 소개)
 //세번째 파라미터 parent : 이 Adapter 객체가 설정된 AdapterView객체(이 예제에서는 ListView)

 @Override
 public View getView(int position, View convertView, ViewGroup parent) {

  //크게 getView의 안에서는 2가지 작업이 이루어 집니다.
  //1. ListView의 목록 하나의 모양을 담당하는 View 객체를 만들어 내는 'New View'
  //2. 만들어진 View에 해당 Data를 연결하는 'Bind View'

  //1.New View
  //지문의 한계상 자세히는 설명하기 어려우니 세부사항은 다른 포스트를 참고하시기 바랍니다.
  //가장 먼저 new View를 하기 위해서 convertView 객체가 null인지 확인합니다.
  //null을 확인하는 이유는 자원 재활용때문인데..
  //짧게 설명하자면.. ListView에서 보여줄 목록이 만약 100개라면...
  //데이터의 양이 많아 분명 동시에 보여질 수 있는 목록의 개수를 정해져 있을 겁니다.
  //그래서 이전 예제에서 보았듯이 ListView는 개수가 많으면 자동으로 Scroll되도록 되어 있지요.
  //여기서 조금 생각해보시면 간단한데요.. 한번에 만약 5개 밖에 못보여진다면...
  //굳이 나머지 95개의 View를 미리 만들 필요가 없겠죠.. 어차피 한번에 보여지는게 5~6개 수준이라면..
  //그 정보의 View만 만들어서 안에 데이터만 바꾸면 메모리를 아낄 수 있다고 생각한 겁니다.
  //여기 전달되어 체크되고 있는 converView 객체가 재활용할 View가 있으면 null이 아닌값을 가지고
  //있다고 보시면 되고 converView가 null이면 새로 만들어야 한다고 보시면 됩니다.

  if( convertView==null){
   //null이라면 재활용할 View가 없으므로 새로운 객체 생성
   //View의 모양은 res폴더>>layout폴더>>list.xml 파일로 객체를 생성
   //xml로 선언된 레이아웃(layout)파일을 객체로 부풀려주는 LayoutInflater 객체 활용.

   convertView= inflater.inflate(R.layout.list_row, null);
  }

  //2. Bind View
  //재활용을 하든 새로 만들었든 이제 converView는 View객체 상태임.
  //이 convertView로부터 데이터를 입력할 위젯들 참조하기.
  //이름을 표시하는 TextView, 국적을 표시하는 TextView, 국기이미지를 표시하는 ImageView
  //convertView로 부터 findViewById()를 하시는 것에 주의하세요.

  TextView text_name= (TextView)convertView.findViewById(R.id.text_name);
  TextView text_nation= (TextView)convertView.findViewById(R.id.text_nation);
  ImageView img_flag= (ImageView)convertView.findViewById(R.id.img_flag);

  //현재 position( getView()메소드의 첫번재 파라미터 )번째의 Data를 위 해당 View들에 연결..

  text_name.setText( datas.get(position).getName() );
  text_nation.setText( datas.get(position).getNation() );
  img_flag.setImageResource( datas.get(position).getImgId() );

  //설정이 끝난 convertView객체 리턴(ListView에서 목록 하나.)
  return convertView;

  ArrayList<Weather> weathers;
    LayoutInflater inflater;

    public FutureWeatherListAdapter(LayoutInflater inflater, ArrayList<Weather> weathers) {
        this.inflater = inflater;
        this.weathers = weathers;
    }

    @Override
    public int getCount() {
        //특별한 경우가 아니라면 보통 리스트의 size 를 리턴함.
        return this.weathers.size();
    }

    @Override
    public Object getItem(int i) {
        return this.weathers.get(i);
    }

    @Override
    public long getItemId(int position) {
        //특별한 경우가 아니라면 보통은 data 의 위치를 아이디로 사용하므로
        //전달받은 position 를 그대로 리턴함.
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //없으면 새로 생성
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.list_column, null);
        }

        TextView tv_futureTime = convertView.findViewById(R.id.tv_futureTime);
        TextView tv_futureRain = convertView.findViewById(R.id.tv_futureRain);
        TextView tv_futureTemp = convertView.findViewById(R.id.tv_futureTemp);
        ImageView img_flag = convertView.findViewById(R.id.iv_futureImg);

        FutureWeather futureWeather = (FutureWeather) this.weathers.get(position);

        tv_futureTime.setText(futureWeather.getDate());
        tv_futureRain.setText(futureWeather.getRainProbability());
        tv_futureTemp.setText(futureWeather.getTemperature());
        img_flag.setImageResource(R.drawable.cloudy);

        return convertView;
    }







  */
