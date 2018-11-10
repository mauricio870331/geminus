/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author admin
 */
@Named(value = "turnosBeans")
@SessionScoped
public class turnosBeans implements Serializable {

    /**
     * Creates a new instance of turnosBeans
     */
    public turnosBeans() {
    }
    
}
