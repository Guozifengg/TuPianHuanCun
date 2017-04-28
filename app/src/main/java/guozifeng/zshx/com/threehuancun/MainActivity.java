package guozifeng.zshx.com.threehuancun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private MyImageLoader myImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);

        myImageLoader = MyImageLoader.getInstance();
        MyBase myBase = new MyBase();
        lv.setAdapter(myBase);
    }

    class MyBase extends BaseAdapter{

        @Override
        public int getCount() {
            return Picture.ImageArray.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                viewHolder = new ViewHolder();
                convertView=View.inflate(MainActivity.this,R.layout.lv_item,null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }

            myImageLoader.display(viewHolder.imageView,Picture.ImageArray[position]);
            return convertView;
        }
    }

    class ViewHolder{

        public ImageView imageView;
    }
}
