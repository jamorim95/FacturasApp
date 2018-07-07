package com.asuswork.jamor.facturasapp.Database.Facturas;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by jamor on 28/02/2018.
 */

public class FacturaCursorWrapper extends CursorWrapper {


    public FacturaCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Factura getFactura(){
        Factura f = new Factura();

        String ID = getString(getColumnIndex(FacturaDbScheme.FacturaTable.Cols.ID));
        String data = getString(getColumnIndex(FacturaDbScheme.FacturaTable.Cols.DATA));
        String comentario = getString(getColumnIndex(FacturaDbScheme.FacturaTable.Cols.COMENTARIO));
        String user = getString(getColumnIndex(FacturaDbScheme.FacturaTable.Cols.USER));


        f.setID(ID);
        f.setData(data);
        f.setComentario(comentario);
        f.setUser(user);

        return f;
    }

    public String getFacturaID(){
        String ID = getString(getColumnIndex(FacturaDbScheme.FacturaTable.Cols.ID));
        return ID;
    }
}
