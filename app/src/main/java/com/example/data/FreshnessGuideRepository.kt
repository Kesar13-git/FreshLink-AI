package com.example.data

import com.example.model.FreshnessGuideItem

/**
 * Repository providing produce freshness inspection guidance and post-harvest storage tips
 * specifically tailored for consumers and inexperienced grocery buyers.
 */
object FreshnessGuideRepository {

  val guideItems: List<FreshnessGuideItem> = listOf(
    FreshnessGuideItem(
      id = "tomato",
      produceName = "Tomato",
      category = "Fruiting Vegetable",
      positiveSigns = listOf(
        "Look for deep, uniform red or variety-typical color",
        "Firm to the touch with slight gentle give",
        "Fresh green calyx (stem crown) with aromatic scent",
        "Smooth, glossy, taut skin without deep fissures"
      ),
      warningSigns = listOf(
        "Excessive soft, watery depressions or pitting",
        "Visible dark mold spots or fungal mycelium around stem",
        "Leaking juice or sour, fermented aroma",
        "Severe radial cracking with discoloration"
      ),
      ripenessStages = listOf(
        "Breaker / Turning: Pale orange-pink, 5–7 days window",
        "Ripe: Vibrant red, firm, optimal sweet-tart balance",
        "Overripe: Very soft, wrinkling skin, immediate culinary use recommended"
      ),
      storageAdvice = "Store stem-side down at room temperature (18–22°C) away from direct sunlight. Never refrigerate unripe tomatoes as cold temperatures below 12°C break down volatile flavor compounds and create a mealy texture.",
      idealTemperature = "18°C – 22°C (Room Temp)",
      typicalShelfLife = "4 – 7 days"
    ),
    FreshnessGuideItem(
      id = "apple",
      produceName = "Apple",
      category = "Pome Fruit",
      positiveSigns = listOf(
        "Look for a firm, dense appearance with good weight in hand",
        "Natural skin luster without excessive oiliness",
        "Tight, unbroken skin with crisp resistance when gently pressed",
        "Fresh, intact woody stem"
      ),
      warningSigns = listOf(
        "Deep brown, soft bruises penetrating through the flesh",
        "Spongy or hollow feeling indicating internal breakdown",
        "Punctures, cuts, or insect bore holes in the skin",
        "Wrinkled, dull skin indicating severe dehydration"
      ),
      ripenessStages = listOf(
        "Crisp Harvest: Firm flesh, tartness, 3–4 weeks cold storage",
        "Peak Eating: Balanced sweetness, firm bite",
        "Mealy / Over-stored: Soft texture, loss of juiciness"
      ),
      storageAdvice = "Refrigerate in the crisper drawer inside a perforated plastic or breathable bag to retain humidity. Keep separated from other produce as apples emit ethylene gas which accelerates ripening of nearby vegetables.",
      idealTemperature = "1°C – 4°C (Refrigerated)",
      typicalShelfLife = "2 – 4 weeks"
    ),
    FreshnessGuideItem(
      id = "banana",
      produceName = "Banana",
      category = "Tropical Fruit",
      positiveSigns = listOf(
        "Green-yellow transition indicates excellent shelf window",
        "Clean, intact yellow peel with small natural sugar speckles",
        "Sturdy stem cluster without splitting",
        "Firm, uniform cylinder shape along the curve"
      ),
      warningSigns = listOf(
        "Extensive dark, mushy soft areas across the peel",
        "Split peels exposing the inner fruit to air and microbes",
        "Grayish dull cast indicating past cold damage during transit",
        "Fungal decay or black mold at the crown connector"
      ),
      ripenessStages = listOf(
        "Green Tip: Starchy, firm, 4–6 days remaining",
        "Full Yellow: Peak sweetness, smooth creamy texture, 2–3 days",
        "Sugar-Spotted: Very sweet, softer flesh, ideal for smoothies or baking, 1–2 days"
      ),
      storageAdvice = "Store hanging on a banana hook or flat on a countertop at room temperature. Keep away from heat sources. To slow down ripening, wrap the crown/stem tips tightly with cling film or foil.",
      idealTemperature = "15°C – 20°C (Room Temp)",
      typicalShelfLife = "3 – 6 days"
    ),
    FreshnessGuideItem(
      id = "spinach",
      produceName = "Spinach & Leafy Greens",
      category = "Leafy Vegetable",
      positiveSigns = listOf(
        "Vibrant, deep green leaves with crisp cellular structure",
        "Sturdy, snapping stems with high hydration",
        "Clean, dry leaf surfaces without slime",
        "Fresh earthy aroma without pungent odor"
      ),
      warningSigns = listOf(
        "Slimy, dark translucent patches on leaf blades",
        "Extensive yellowing (chlorosis) or wilting",
        "Musty, sour, or stagnant water smell",
        "Blackened stem ends or leaf rot"
      ),
      ripenessStages = listOf(
        "Harvest Fresh: Crisp, springy, peak vitamins, 3–5 days",
        "Wilted: Loss of water tension, usable for cooked dishes immediately",
        "Decayed: Slimy or foul-smelling, do not consume"
      ),
      storageAdvice = "Wrap loosely in dry paper towels to absorb excess moisture, and store inside an airtight container in the refrigerator crisper. Do NOT wash until right before consumption.",
      idealTemperature = "1°C – 3°C (Refrigerated)",
      typicalShelfLife = "3 – 5 days"
    ),
    FreshnessGuideItem(
      id = "bell_pepper",
      produceName = "Bell Pepper",
      category = "Fruiting Vegetable",
      positiveSigns = listOf(
        "Taut, reflective glossy outer skin with no wrinkles",
        "Heavy for its size with thick, firm flesh walls",
        "Fresh, vibrant green stem without dryness or mold",
        "Bright, saturated coloring across all lobes"
      ),
      warningSigns = listOf(
        "Wrinkled, loose skin indicating moisture loss",
        "Water-soaked depressions or soft spots on sides",
        "Black mold specks around the inner stem cap",
        "Hollow, dry or brittle stem"
      ),
      ripenessStages = listOf(
        "Crisp & Firm: Maximum crunch, sweet aroma, 7–10 days",
        "Softening: Slight give, best cooked or roasted, 2–3 days",
        "Collapsing: Watery decay, discard"
      ),
      storageAdvice = "Store dry and unwashed in the crisper drawer of your refrigerator. Place in a mesh produce bag for optimal airflow. If cut, wrap remaining half tightly and consume within 2 days.",
      idealTemperature = "7°C – 10°C (Crisper)",
      typicalShelfLife = "1 – 2 weeks"
    )
  )
}
