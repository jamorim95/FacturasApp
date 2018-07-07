package com.asuswork.jamor.facturasapp.Activities.ListaProdutos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Users.UsersBaseHelper;
import com.asuswork.jamor.facturasapp.Activities.MainActivity;
import com.asuswork.jamor.facturasapp.Activities.ListaProdutos.MasterFlowDummy.ListaProdutosContent;
import com.asuswork.jamor.facturasapp.R;

import java.util.List;

/**
 * An activity representing a list of ProdutoElements. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProdutoElementDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ProdutoElementListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtoelement_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        username = getIntent().getStringExtra("username");

        //TODO: confirmar se isto é aqui
        ListaProdutosContent.mDatabase_produtos = new ProdutoBaseHelper(getApplicationContext()).getReadableDatabase();
        ListaProdutosContent.mDatabase_relacaoFacturaProdutos = new RelacaoFacturaProdutoBaseHelper(getApplicationContext()).getReadableDatabase();
        ListaProdutosContent.mDatabase_users = new UsersBaseHelper(getApplicationContext()).getReadableDatabase();
        ListaProdutosContent.username=username;
        ListaProdutosContent.iniciar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Voltar atrás", Snackbar.LENGTH_LONG)
                        .setAction("VOLTAR", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
            }
        });

        if(ListaProdutosContent.ITEMS.isEmpty()) {
            Toast.makeText(this, "Não há produtos para mostrar !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        }

        View recyclerView = findViewById(R.id.produtoelement_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.produtoelement_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        this.finish();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ListaProdutosContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ListaProdutosContent.ListaProdutosItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<ListaProdutosContent.ListaProdutosItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.produtoelement_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getProdutoID());
            holder.mContentView.setText(mValues.get(position).getProduto().toString_masterFlow());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ProdutoElementDetailFragment.ARG_ITEM_ID, holder.mItem.getProdutoID());
                        ProdutoElementDetailFragment fragment = new ProdutoElementDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.produtoelement_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ProdutoElementDetailActivity.class);
                        intent.putExtra(ProdutoElementDetailFragment.ARG_ITEM_ID, holder.mItem.getProdutoID());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public ListaProdutosContent.ListaProdutosItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
