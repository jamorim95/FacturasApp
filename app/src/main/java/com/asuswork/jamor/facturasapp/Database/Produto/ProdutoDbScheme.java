package com.asuswork.jamor.facturasapp.Database.Produto;

/**
 * Created by jamor on 28/02/2018.
 */

public class ProdutoDbScheme {
    public static final class ProdutoTable{
        public static final String NAME = "produtos";

        public static final class Cols{
            public static final String ID = "id";
            public static final String NOME = "nome";
            public static final String VALOR = "valor";
            public static final String CATEGORIA = "categoria";
            public static final String DATA = "data";
            public static final String COMENTARIO = "comentario";
            public static final String USER = "user";
        }
    }
}
