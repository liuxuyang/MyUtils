// ISmsAidlInterface.aidl
package priv.liuxy.pushsmsdome;

// Declare any non-default types here with import statements

interface ISmsAidlInterface {
    void pushData(String smsBody,String smsAddress,long time);
}
