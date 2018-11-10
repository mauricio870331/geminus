///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package Beans;
//
//import Model.notify;
//import javax.inject.Named;
//
//import java.io.Serializable;
//import javax.enterprise.context.SessionScoped;
//import javax.faces.context.FacesContext;
//import javax.faces.view.ViewScoped;
//
///**
// *
// * @author admin
// */
//@Named(value = "mesagge")
//@SessionScoped
//public class Mesagge implements Serializable {
//
//    notify Notify;
//
//    public Mesagge() {
//    }
//    
//    public String redirect(){
//       return Notify.getSalida();
//    }
//            
//
//    public notify getNotify() {
//        if(Notify==null){
//            Notify=(notify)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("mns");
//        }
//        return Notify;
//    }
//
//    public void setNotify(notify Notify) {
//        this.Notify = Notify;
//    }
//    
//    
//
//}
