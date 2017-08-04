package com.techscan.dvq.common;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuya on 2017/6/21.
 * ������
 */

public class Utils {

    public static final int HANDER_DEPARTMENT   = 1;
    public static final int HANDER_STORG        = 2;
    public static final int HANDER_SAVE_RESULT  = 3;
    public static final int HANDER_POORDER_HEAD = 4;
    public static final int HANDER_POORDER_BODY = 5;

    public static String formatTime(long time) {
        java.util.Date   date = new java.util.Date(time);
        SimpleDateFormat df   = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * ++++++++++++++
     * ��ʽ��С����һ�ɸ�ʽΪ��λС��
     *
     * @param num ����Ϊfloat����
     * @return �ַ���
     */
    public static String formatDecimal(float num) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");//���췽�����ַ���ʽ�������С������2λ,����0����.
        String        s             = decimalFormat.format(num);
        return s;
    }

    public static String formatDecimal(double num) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");//���췽�����ַ���ʽ�������С������2λ,����0����.
        String        s             = decimalFormat.format(num);
        return s;
    }

    public static String formatDecimal(String num) {
        float n = Float.valueOf(num);
        return formatDecimal(n);
    }

    /**
     * �ж��Ƿ������֣�ʹ��������ʽ
     *
     * @param str ��ƥ����ַ���
     * @return ƥ����, �����ַ���true, �������ַ���false
     */
    public static boolean isNumber(@NonNull String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum   = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * ��ʾ����ķ��ؽ������Ϣ
     *
     * @param message dialog������Ϣ
     */
    public static void showResultDialog(Context context, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("������Ϣ");
        dialog.setMessage(message);
        dialog.setPositiveButton("�ر�", null);
        dialog.show();
    }
}
