package com.mmanchola.blog.dao;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class PersonDataAccessServiceTest {
//
//  @Autowired
//  PersonDataAccessService dataAccessService;
//  @Autowired
//  PasswordEncoder passwordEncoder;
//
//  @Test
//  public void canPerformCRUD() {
//    String email = "midasama3124@hotmail.com";
//    String password = passwordEncoder.encode("123");
//    UUID id = UUID.randomUUID();
//    Person person = new Person(
//        id,
//        "Miguel",
//        "Manchola",
//        "MALE",
//        (short)24,
//        email,
//        password,
//        new Timestamp(System.currentTimeMillis()),
//        new Timestamp(System.currentTimeMillis())
//    );
//
//    // Create
//    int rowsAffected = dataAccessService.save(person);
//    assertEquals(1, rowsAffected);   // Must have added a new row
//
//    // Read
////    Person personRetrieved = dataAccessService.findByEmail(email).get();
////    assert personRetrieved.equals(person);
//  }
//
//
//}