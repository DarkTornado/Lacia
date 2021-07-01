package com.darktornado.lacia;

import java.util.ArrayList;

public class ChatModule {

    private String[] data;
    private String input;

    public void setData(String[] data) {
        this.data = data;
    }

    public void inputChat(String input) {
        this.input = input;
    }

    public String[] getResult() {
        ArrayList<String> chats = new ArrayList<>();
        String[] input = this.input.split(" ");
        int max = 1;
        for (int n = 0; n < data.length - 1; n++) {
            int count = getCount(data[n], input);
            if (count > max) {
                chats.clear();
                max = count;
            }
            if (count == max) chats.add(data[n + 1]);
        }
        if(chats.size()==0) return null;
        return (String[]) chats.toArray(new String[chats.size()]);
    }

    private int getCount(String data, String[] input) {
        int count = 0;
        for (int n = 0; n < input.length; n++) {
            if (data.contains(input[n])) count++;
        }
        return count;
    }

}
