package com.yyggee.pluto.utils;

import java.util.List;
import java.util.Random;

public class NameGenerator {

  private static final Random random = new Random();

  // Chinese surnames (family names)
  private static final List<String> CHINESE_SURNAMES =
      List.of(
          "Wang", "Li", "Zhang", "Liu", "Chen", "Yang", "Huang", "Zhao", "Wu", "Zhou", "Xu", "Sun",
          "Ma", "Zhu", "Hu", "Guo", "Lin", "He", "Gao", "Liang", "Zheng", "Luo", "Song", "Xie",
          "Tang", "Han", "Cao", "Xu", "Deng", "Feng");

  // Chinese given names (single characters combined)
  private static final List<String> CHINESE_GIVEN_NAMES =
      List.of(
          "Wei", "Fang", "Min", "Jing", "Hui", "Ying", "Lei", "Xia", "Yan", "Mei", "Hong", "Juan",
          "Qiang", "Jun", "Ping", "Gang", "Dong", "Cheng", "Bo", "Hao", "Yi", "Xin", "Yu", "Zhi",
          "Tao", "Wen", "Long", "Feng", "Kai", "Ming");

  // Malaysian Malay names
  private static final List<String> MALAY_FIRST_NAMES =
      List.of(
          "Ahmad",
          "Muhammad",
          "Mohd",
          "Abdul",
          "Ali",
          "Hassan",
          "Ibrahim",
          "Ismail",
          "Omar",
          "Yusof",
          "Fatimah",
          "Siti",
          "Nur",
          "Aisyah",
          "Aminah",
          "Zainab",
          "Noraini",
          "Rohani",
          "Haslinda",
          "Azizah");

  private static final List<String> MALAY_PATRONYMS =
      List.of(
          "bin Abdullah",
          "bin Ahmad",
          "bin Hassan",
          "bin Ibrahim",
          "bin Ismail",
          "bin Omar",
          "bin Yusof",
          "bin Osman",
          "bin Razak",
          "bin Hamid",
          "binti Abdullah",
          "binti Ahmad",
          "binti Hassan",
          "binti Ibrahim",
          "binti Ismail",
          "binti Omar",
          "binti Yusof",
          "binti Osman",
          "binti Razak",
          "binti Hamid");

  // Malaysian Chinese names
  private static final List<String> MALAYSIAN_CHINESE_SURNAMES =
      List.of(
          "Tan", "Lim", "Lee", "Ng", "Wong", "Ong", "Goh", "Chua", "Chan", "Koh", "Low", "Teo",
          "Yap", "Sim", "Chong", "Foo", "Chin", "Heng", "Yeoh", "Chew");

  // Malaysian Indian names
  private static final List<String> INDIAN_FIRST_NAMES =
      List.of(
          "Rajesh", "Kumar", "Suresh", "Ramesh", "Ganesh", "Vijay", "Arun", "Siva", "Muthu", "Ravi",
          "Priya", "Lakshmi", "Devi", "Rani", "Meena", "Kavitha", "Selvi", "Malliga", "Valli",
          "Pushpa");

  private static final List<String> INDIAN_PATRONYMS =
      List.of(
          "a/l Rajan",
          "a/l Kumar",
          "a/l Muthusamy",
          "a/l Krishnan",
          "a/l Subramaniam",
          "a/l Ramasamy",
          "a/l Muniandy",
          "a/l Suppiah",
          "a/l Govindasamy",
          "a/l Arumugam",
          "a/p Rajan",
          "a/p Kumar",
          "a/p Muthusamy",
          "a/p Krishnan",
          "a/p Subramaniam",
          "a/p Ramasamy",
          "a/p Muniandy",
          "a/p Suppiah",
          "a/p Govindasamy",
          "a/p Arumugam");

  // USA names
  private static final List<String> USA_FIRST_NAMES =
      List.of(
          "James",
          "John",
          "Robert",
          "Michael",
          "William",
          "David",
          "Richard",
          "Joseph",
          "Thomas",
          "Christopher",
          "Mary",
          "Patricia",
          "Jennifer",
          "Linda",
          "Elizabeth",
          "Barbara",
          "Susan",
          "Jessica",
          "Sarah",
          "Karen",
          "Emily",
          "Emma",
          "Olivia",
          "Ava",
          "Isabella",
          "Sophia",
          "Mia",
          "Charlotte",
          "Amelia",
          "Harper",
          "Liam",
          "Noah",
          "Oliver",
          "Elijah",
          "Lucas",
          "Mason",
          "Logan",
          "Alexander",
          "Ethan",
          "Jacob");

  private static final List<String> USA_LAST_NAMES =
      List.of(
          "Smith",
          "Johnson",
          "Williams",
          "Brown",
          "Jones",
          "Garcia",
          "Miller",
          "Davis",
          "Rodriguez",
          "Martinez",
          "Hernandez",
          "Lopez",
          "Gonzalez",
          "Wilson",
          "Anderson",
          "Thomas",
          "Taylor",
          "Moore",
          "Jackson",
          "Martin",
          "Lee",
          "Perez",
          "Thompson",
          "White",
          "Harris",
          "Sanchez",
          "Clark",
          "Ramirez",
          "Lewis",
          "Robinson");

  // Indonesian names
  private static final List<String> INDONESIAN_FIRST_NAMES =
      List.of(
          "Budi",
          "Agus",
          "Andi",
          "Dedi",
          "Eko",
          "Hendra",
          "Joko",
          "Rizki",
          "Wahyu",
          "Yusuf",
          "Dewi",
          "Sari",
          "Putri",
          "Wati",
          "Ningsih",
          "Rahayu",
          "Lestari",
          "Indah",
          "Ayu",
          "Sri",
          "Bambang",
          "Sugeng",
          "Hartono",
          "Santoso",
          "Wibowo",
          "Prasetyo",
          "Nugroho",
          "Kurniawan",
          "Setiawan",
          "Hidayat");

  private static final List<String> INDONESIAN_LAST_NAMES =
      List.of(
          "Susanto",
          "Wijaya",
          "Santoso",
          "Gunawan",
          "Setiawan",
          "Lim",
          "Tanuwijaya",
          "Halim",
          "Pranoto",
          "Suryadi",
          "Kusuma",
          "Permana",
          "Saputra",
          "Pratama",
          "Putra",
          "Cahyadi",
          "Wibowo",
          "Nugraha",
          "Firmansyah",
          "Ramadhan");

  // Javanese single names (no surname)
  private static final List<String> JAVANESE_SINGLE_NAMES =
      List.of(
          "Suharto",
          "Sukarno",
          "Megawati",
          "Prabowo",
          "Jokowi",
          "Susilo",
          "Habibie",
          "Gus Dur",
          "Wahid",
          "Sutrisno",
          "Bambang",
          "Slamet",
          "Suryo",
          "Haryanto",
          "Mulyono",
          "Sutopo",
          "Sunarto",
          "Supriadi",
          "Suyanto",
          "Sugiyono");

  public enum NameType {
    CHINESE,
    MALAYSIAN_MALAY,
    MALAYSIAN_CHINESE,
    MALAYSIAN_INDIAN,
    USA,
    INDONESIAN,
    INDONESIAN_JAVANESE
  }

  public static String generateName(NameType type) {
    return switch (type) {
      case CHINESE -> generateChineseName();
      case MALAYSIAN_MALAY -> generateMalaysianMalayName();
      case MALAYSIAN_CHINESE -> generateMalaysianChineseName();
      case MALAYSIAN_INDIAN -> generateMalaysianIndianName();
      case USA -> generateUsaName();
      case INDONESIAN -> generateIndonesianName();
      case INDONESIAN_JAVANESE -> generateJavaneseName();
    };
  }

  public static String generateRandomName() {
    NameType[] types = NameType.values();
    return generateName(types[random.nextInt(types.length)]);
  }

  public static NameType getRandomType() {
    NameType[] types = NameType.values();
    return types[random.nextInt(types.length)];
  }

  private static String generateChineseName() {
    // Chinese: Surname + 1-2 given name characters
    String surname = randomFrom(CHINESE_SURNAMES);
    String given1 = randomFrom(CHINESE_GIVEN_NAMES);
    if (random.nextBoolean()) {
      String given2 = randomFrom(CHINESE_GIVEN_NAMES);
      return surname + " " + given1 + given2;
    }
    return surname + " " + given1;
  }

  private static String generateMalaysianMalayName() {
    // Malay: First name + bin/binti + Father's name
    return randomFrom(MALAY_FIRST_NAMES) + " " + randomFrom(MALAY_PATRONYMS);
  }

  private static String generateMalaysianChineseName() {
    // Malaysian Chinese: Surname + Given names (Western order sometimes)
    String surname = randomFrom(MALAYSIAN_CHINESE_SURNAMES);
    String given1 = randomFrom(CHINESE_GIVEN_NAMES);
    String given2 = randomFrom(CHINESE_GIVEN_NAMES);
    return surname + " " + given1 + " " + given2;
  }

  private static String generateMalaysianIndianName() {
    // Malaysian Indian: First name + a/l or a/p + Father's name
    return randomFrom(INDIAN_FIRST_NAMES) + " " + randomFrom(INDIAN_PATRONYMS);
  }

  private static String generateUsaName() {
    // USA: First name + Last name
    String firstName = randomFrom(USA_FIRST_NAMES);
    String lastName = randomFrom(USA_LAST_NAMES);
    // Sometimes add middle initial
    if (random.nextInt(3) == 0) {
      char middleInitial = (char) ('A' + random.nextInt(26));
      return firstName + " " + middleInitial + ". " + lastName;
    }
    return firstName + " " + lastName;
  }

  private static String generateIndonesianName() {
    // Indonesian: First name + optional last name
    String firstName = randomFrom(INDONESIAN_FIRST_NAMES);
    if (random.nextBoolean()) {
      return firstName + " " + randomFrom(INDONESIAN_LAST_NAMES);
    }
    return firstName;
  }

  private static String generateJavaneseName() {
    // Javanese: Often single name
    return randomFrom(JAVANESE_SINGLE_NAMES);
  }

  private static String randomFrom(List<String> list) {
    return list.get(random.nextInt(list.size()));
  }
}
