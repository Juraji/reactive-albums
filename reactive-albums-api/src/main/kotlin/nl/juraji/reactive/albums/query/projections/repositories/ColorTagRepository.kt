package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.ColorTagProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ColorTagRepository: JpaRepository<ColorTagProjection, String> {

    @Query(nativeQuery = true, value = """
        select t.* from ColorTagProjection t
        order by (
            POW((:red - t.red) * 0.3, 2) +
            POW((:green - t.green) * 0.59, 2) +
            POW((:blue - t.blue) * 0.11, 2)
        )
        limit 1
    """)
    fun findClosestColorTag(
            @Param("red") red: Int,
            @Param("green") green: Int,
            @Param("blue") blue: Int,
    ): ColorTagProjection
}
