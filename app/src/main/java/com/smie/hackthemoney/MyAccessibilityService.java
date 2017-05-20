package com.smie.hackthemoney;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/5/9.
 */

public class MyAccessibilityService extends AccessibilityService {
    private boolean to_open;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    if(to_open)
                    getPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    openPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    close();
                    to_open=false;
                }


                break;
        }
    }

    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                //如果微信红包的提示信息,则模拟点击进入相应的聊天窗口
                if (content.contains("[微信红包]")) {
                    to_open = true;
                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void close() {
        /*
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            //为了演示,直接查看了关闭按钮的id
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("@id/fs");
            nodeInfo.recycle();
            for (AccessibilityNodeInfo item : infos) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        */

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo nodeInfo = get_close(rootNode);
        if (nodeInfo != null) {

            if(  nodeInfo.isClickable() && nodeInfo.getClassName().toString().equals("android.widget.LinearLayout") )
            {
                Log.i("close: ","success");
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPacket() {
        //AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo nodeInfo = get_click(rootNode);
        if (nodeInfo != null) {

            /*
            //为了演示,直接查看了红包控件的id
            //List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("@id/a3p");
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("@id/bdg");

            Log.i("test: ",String.format("%d",list.size()));

            nodeInfo.recycle();
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            */


            if(  nodeInfo.isClickable() && nodeInfo.getClassName().toString().equals("android.widget.Button") )
            {
                Log.i("get: ","success");
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo node = recycle(rootNode);

        if(node==null)return;

        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        AccessibilityNodeInfo parent = node.getParent();
        while (parent != null) {
            if (parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            parent = parent.getParent();
        }

    }

    public AccessibilityNodeInfo recycle(AccessibilityNodeInfo node) {
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                if ("领取红包".equals(node.getText().toString())) {
                    return node;
                }
            }
        } else {
            for (int i =  node.getChildCount() -1 ; i >=0; i--) {
                if (node.getChild(i) != null) {
                    AccessibilityNodeInfo cur = recycle(node.getChild(i));
                    if(cur.getText()!=null){
                        if ("领取红包".equals(cur.getText().toString())) {
                            return cur;
                        }
                    }
                }
            }
        }
        return node;
    }

    public AccessibilityNodeInfo get_close(AccessibilityNodeInfo node)
    {
        if (node.getChildCount() == 0) {
            if(node.isClickable() && node.getClassName().toString().equals("android.widget.LinearLayout")){
                return node;
            }
        }
        //node.getClass()
        else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    AccessibilityNodeInfo cur = get_close(node.getChild(i));
                    if(cur.isClickable() && cur.getClassName().toString().equals("android.widget.LinearLayout")){
                        return cur;
                    }
                }
            }
        }
        return node;
    }

    public AccessibilityNodeInfo get_click(AccessibilityNodeInfo node)
    {
        if (node.getChildCount() == 0) {
            if(node.isClickable() && node.getClassName().toString().equals("android.widget.Button")){
                return node;
            }
        }
        //node.getClass()
        else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    AccessibilityNodeInfo cur = get_click(node.getChild(i));
                    if(cur.isClickable() && cur.getClassName().toString().equals("android.widget.Button")){
                        return cur;
                    }
                }
            }
        }
        return node;
    }


    @Override
    public void onInterrupt(){}

    @Override
    protected void onServiceConnected(){
        super.onServiceConnected();
    }


}
