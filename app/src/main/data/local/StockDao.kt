package com.example.stockmarketapp.data.local

import androidx.room.*
import retrofit2.http.DELETE

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    )

    @Query("DELETE from companylistingentity")
    suspend fun clearCompanyListings()

    @Query(
        """
            SELECT * from companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListing(query : String) : List<CompanyListingEntity>
}