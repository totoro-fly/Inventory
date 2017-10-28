package com.totoro.inventory.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.totoro.inventory.R;
import com.totoro.inventory.adapter.ProductCursorAdapter;
import com.totoro.inventory.data.ProductContract;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.tv_product_name)
    TextView tvProductName;
    @Bind(R.id.tv_product_amount)
    TextView tvProductAmount;
    @Bind(R.id.tv_product_price)
    TextView tvProductPrice;
    @Bind(R.id.tv_product_sale)
    TextView tvProductSale;
    @Bind(R.id.lv_product)
    ListView lvProduct;
    @Bind(R.id.fb_add_product)
    FloatingActionButton fbAddProduct;
    @Bind(R.id.tv_empty)
    TextView tvEmpty;
    private static final int PRODUCT_LOADER = 0;
    private ProductCursorAdapter productCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        productCursorAdapter = new ProductCursorAdapter(this, null);
        lvProduct.setEmptyView(tvEmpty);
        lvProduct.setAdapter(productCursorAdapter);
        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProductActivity.this, ParticularActivity.class);
//                Log.d("TAG", String.valueOf(id));
                Uri currentPeturi = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
                intent.setData(currentPeturi);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @OnClick(R.id.fb_add_product)
    public void onViewClicked() {
        Intent intent = new Intent(this, ParticularActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_data:
                deleteAllProdut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllProdut() {
        int rowsDelete = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
        Toast.makeText(this, "删除 " + rowsDelete + " 行货物", Toast.LENGTH_LONG).show();

    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_AMOUNT,
                ProductContract.ProductEntry.COLUME_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SALE
        };
        return new android.content.CursorLoader(
                this,
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        productCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);

    }
}
