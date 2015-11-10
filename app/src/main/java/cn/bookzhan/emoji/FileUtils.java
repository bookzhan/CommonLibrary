package cn.bookzhan.emoji;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * 读取资源文件,并把映射存起来
 */
public class FileUtils {
	/**
	 * 读取表情配置文件
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		InputStream in=null;
		BufferedReader br=null;
		try {
			List<String> list = new ArrayList<String>();
			in = context.getResources().getAssets().open("emoji");
			br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
