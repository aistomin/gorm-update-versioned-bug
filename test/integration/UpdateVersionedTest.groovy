/**
 * Created by aistomin on 10/12/2016.
 *
 * Test that demonstrates the "UPDATE VERSIONED" queries bug in GORM/Hibernate.
 */
class UpdateVersionedTest extends GroovyTestCase {

    /**
     * Check "UPDATE VERSIONED" HQL query.
     */
    void testUpdateVersionedBug() {
        final def entity = new Child(name: 'Test').save()
        final def version = entity.version
        Parent.executeUpdate(
            'UPDATE VERSIONED Parent p SET p.name = :val WHERE p.id = :id',
            [id: entity.id, val: 'Updated value']
        )
        assertEquals(version + 1, entity.refresh().version)
    }
}
