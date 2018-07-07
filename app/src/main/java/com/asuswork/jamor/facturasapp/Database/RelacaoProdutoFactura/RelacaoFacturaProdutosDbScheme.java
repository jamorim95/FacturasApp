package com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura;

/**
 * Created by jamor on 28/02/2018.
 */

public class RelacaoFacturaProdutosDbScheme {
    public static final class RelacaoFacturaProdutosTable{
        public static final String NAME = "relacao_factura_produtos";

        public static final class Cols{
            public static final String ID_FACTURA = "id_factura";
            public static final String ID_PRODUTO = "id_produto";
        }
    }
}
