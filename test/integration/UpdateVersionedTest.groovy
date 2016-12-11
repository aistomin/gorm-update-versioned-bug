import org.hibernate.Session

/**
 * Created by aistomin on 10/12/2016.
 *
 * This tests suite demonstrates the "UPDATE VERSIONED" queries bug in
 * GORM/Hibernate and one of the possible workarounds.
 */
class UpdateVersionedTest extends GroovyTestCase {

    /**
     * This test demonstrates the bug.
     */
    void testUpdateVersionedBug() {
        final def entity = new Child(name: 'Test').save()
        final def version = entity.version
        Parent.executeUpdate(
            'UPDATE VERSIONED Parent p SET p.name = :val WHERE p.id IN (:ids)',
            [ids: [entity.id], val: 'Updated value']
        )
        assertEquals(version + 1, entity.refresh().version)
    }

    /**
     * This test demonstrates the workaround.
     */
    void testUpdateVersionedBugWorkaround() {
        final def entity = new Child(name: 'Test').save()
        final def version = entity.version
        Parent.withSession { final Session session ->
            final def query = session.createSQLQuery(
                'UPDATE parent p SET p.name = :val, p.version = p.version + 1 WHERE p.id IN (:ids)'
            )
            query.setLong('ids', entity.id)
            query.setString('val', 'Updated value')
            query.executeUpdate()
        }
        assertEquals(version + 1, entity.refresh().version)
    }
}
