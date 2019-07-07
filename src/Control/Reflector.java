/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Azhary Arliansyah
 */
public class Reflector {
    
    public static Object callUserFunc(Class<?> cls, Object obj, 
            String methodName) {
        Method method;
        try {
            method = cls.getMethod(methodName, null);
            return method.invoke(obj, null);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Reflector.class.getName()).log(Level.SEVERE, 
                    null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Reflector.class.getName()).log(Level.SEVERE, 
                    null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Reflector.class.getName()).log(Level.SEVERE, 
                    null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Reflector.class.getName()).log(Level.SEVERE, 
                    null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Reflector.class.getName()).log(Level.SEVERE, 
                    null, ex);
        }
        return null;
    }
    
}
