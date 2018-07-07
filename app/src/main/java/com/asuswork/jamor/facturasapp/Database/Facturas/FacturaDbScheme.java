package com.asuswork.jamor.facturasapp.Database.Facturas;

/**
 * Created by jamor on 28/02/2018.
 */

public class FacturaDbScheme {
    public static final class FacturaTable{
        public static final String NAME = "facturas";

        public static final class Cols{
            public static final String ID = "id";
            public static final String DATA = "data";
            public static final String COMENTARIO = "comentario";
            public static final String USER = "user";
            public static final String DESIGNACAO = "designacao";
        }
    }
}
