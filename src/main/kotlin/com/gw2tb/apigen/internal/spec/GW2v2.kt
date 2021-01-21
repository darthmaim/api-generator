/*
 * Copyright (c) 2019-2021 Leon Linhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@file:Suppress("DuplicatedCode", "FunctionName")
package com.gw2tb.apigen.internal.spec

import com.gw2tb.apigen.internal.dsl.*
import com.gw2tb.apigen.model.TokenScope.*
import com.gw2tb.apigen.model.v2.V2SchemaVersion.*
import kotlin.time.*

internal val GW2v2 = GW2APIVersion {
    "/Account" {
        summary = "Returns information about a player's account."
        security(ACCOUNT)

        schema(record(name = "Account", description = "Information about a player's account.") {
            CamelCase("id").."ID"(STRING, "the unique persistent account GUID")
            "Age"(INTEGER, "the age of the account in seconds")
            "Name"(STRING, "the unique account name")
            "World"(INTEGER, "the ID of the home world the account is assigned to")
            "Guilds"(array(STRING), "an array containing the IDs of all guilds the account is a member in")
            optional(GUILDS)..SerialName("guild_leader").."GuildLeader"(array(STRING), "an array containing the IDs of all guilds the account is a leader of")
            "Created"(STRING, "the ISO-8601 standard timestamp of when the account was created")
            "Access"(array(STRING), "an array of what content this account has access to")
            "Commander"(BOOLEAN, "a flag indicating whether or not the commander tag is unlocked for the account")
            optional(PROGRESSION)..SerialName("fractal_level").."FractalLevel"(INTEGER, "the account's personal fractal level")
            optional(PROGRESSION)..SerialName("daily_ap").."DailyAP"(INTEGER, "the daily AP the account has")
            optional(PROGRESSION)..SerialName("monthly_ap").."MonthlyAP"(INTEGER, "the monthly AP the account has")
            optional(PROGRESSION)..CamelCase("wvwRank")..SerialName("wvw_rank").."WvWRank"(INTEGER, "the account's personal wvw rank")
            since(V2_SCHEMA_2019_02_21T00_00_00_000Z)..SerialName("last_modified").."LastModified"(STRING, "the ISO-8601 standard timestamp of when the account information last changed (as perceived by the API)")
        })
    }
    "/Account/Achievements" {
        summary = "Returns a player's progress towards all their achievements."
        security = setOf(ACCOUNT, PROGRESSION)

        schema(array(
            description = "A list of progress information towards all achievements the account has made progress.",
            items = record(name = "AccountAchievement", description = "Information about a player's progress towards an achievement.") {
                CamelCase("id").."ID"(INTEGER, "the achievement's ID")
                "Done"(BOOLEAN, "a flag indicating whether or not the account has completed the achievement")
                optional.."Bits"(array(INTEGER), "an array of numbers (whose exact meaning differs) giving information about the progress towards an achievement")
                optional.."Current"(INTEGER, "the account's current progress towards the achievement")
                optional.."Max"(INTEGER, "the amount of progress required to complete the achievement")
                optional.."Repeated"(INTEGER, "the number of times the achievement has been completed (if the achievement is repeatable)")
                optional.."Unlocked"(BOOLEAN, "a flag indicating whether or not the achievement is unlocked (if the achievement can be unlocked)")
            }
        ))
    }
    "/Account/Bank" {
        summary = "Returns information about the items stored in a player's vault."
        security = setOf(ACCOUNT, INVENTORIES)

        schema(array(
            description = "a list of slots in a player's bank.",
            nullableItems = true,
            items = record(name = "AccountBankSlot", description = "Information about a bank slot.") {
                CamelCase("id").."ID"(INTEGER, "the item's ID")
                "Count"(INTEGER, "the amount of items in the stack")
                optional.."Charges"(INTEGER, "the amount of charges remaining on the item")
                optional.."Skin"(INTEGER, "the ID of the skin applied to the item")
                optional.."Upgrades"(array(INTEGER), "the array of item IDs of runes or sigils applied to the item")
                optional.."Infusions"(array(INTEGER), "the array of item IDs of infusions applied to the item")
                optional.."Stats"(
                    description = "contains information on the stats chosen if the item offers an option for stats/prefix",
                    type = record(description = "Information about an item's stats.") {
                        CamelCase("id").."ID"(INTEGER, "the itemstat ID")
                        optional..SerialName("Power").."Power"(INTEGER, "the amount of power given by the item")
                        optional..SerialName("Precision").."Precision"(INTEGER, "the amount of precision given by the item")
                        optional..SerialName("Toughness").."Toughness"(INTEGER, "the amount of toughness given by the item")
                        optional..SerialName("Vitality").."Vitality"(INTEGER, "the amount of vitality given by the item")
                        optional..SerialName("ConditionDamage").."ConditionDamage"(INTEGER, "the amount of condition damage given by the item")
                        optional..SerialName("ConditionDuration").."ConditionDuration"(INTEGER, "the amount of condition duration given by the item")
                        optional..SerialName("Healing").."Healing"(INTEGER, "the amount of healing given by the item")
                        optional..SerialName("BoonDuration").."BoonDuration"(INTEGER, "the amount of boon duration given by the item")
                    }
                )
                optional.."Binding"(STRING, "the binding of the material")
                optional..SerialName("bound_to").."BoundTo"(STRING, "name of the character the item is bound to")
            }
        ))
    }
    "/Account/BuildStorage" {
        summary = "Returns an account's build storage."
        security = setOf(ACCOUNT)

        schema(array(
            description = "the builds in the account's storage",
            items = record(name = "AccountBuildStorageSlot", description = "Information about a build in an account's storage.") {
                "Name"(STRING, "the build's name")
                "Profession"(STRING, "the profession's ID")
                "Specializations"(
                    description = "the build's specializations",
                    type = array(
                        nullableItems = true,
                        items = record(name = "Specialization", description = "Information about a build's specialization.") {
                            CamelCase("id").."ID"(INTEGER, "the specializations ID")
                            "Traits"(array(INTEGER, nullableItems = true), "the trait IDs")
                        }
                    )
                )
                optional.."Skills"(
                    description = "the build's skills",
                    type = record(name = "Skills", description = "Information about a build's skills.") {
                        optional.."Heal"(INTEGER, "the heal skill's ID")
                        "Utilities"(array(INTEGER, nullableItems = true), "the IDs of the utility skills")
                        optional.."Elite"(INTEGER, "the elite skill's ID")
                    }
                )
                optional..SerialName("aquatic_skills").."AuqaticSkills"(
                    description = "the build's aquatic skills",
                    type = record(name = "AuqaticSkills", description = "Information about a build's aquatic skills.") {
                        optional.."Heal"(INTEGER, "the heal skill's ID")
                        "Utilities"(array(INTEGER, nullableItems = true), "the IDs of the utility skills")
                        optional.."Elite"(INTEGER, "the elite skill's ID")
                    }
                )
                optional.."Legends"(array(STRING, nullableItems = true), "the build's legend IDs")
                optional..SerialName("aquatic_legends").."AquaticLegends"(array(STRING, nullableItems = true), "the build's aquatic legend IDs")
            }
        ))
    }
    "/Account/DailyCrafting" {
        summary = "Returns which items that can be crafted once per day a player crafted since the most recent daily reset."
        security = setOf(ACCOUNT, PROGRESSION, UNLOCKS)

        schema(array(STRING, "a list of dailycrafting IDs of the items that can be crafted once per day which the player has crafted since the most recent daily reset"))
    }
    "/Account/Dungeons" {
        summary = "Returns which dungeons paths a player has completed since the most recent daily reset."
        security = setOf(ACCOUNT, PROGRESSION)

        schema(array(STRING, "an array of IDs containing an ID for each dungeon path that the player has completed since the most recent daily reset"))
    }
    "/Account/Dyes" {
        summary = "Returns information about a player's unlocked dyes."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "a list of the dye IDs of the account's unlocked dyes"))
    }
    "/Account/Emotes" {
        summary = "Returns information about a player's unlocked emotes."
        security = setOf(ACCOUNT)

        schema(array(STRING, "an array of IDs containing the ID of each emote unlocked by the player"))
    }
    "/Account/Finishers" {
        summary = "Returns information about a player's unlocked finishers."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(
            description = "the finishers unlocked by the account",
            items = record(name = "AccountFinisher", description = "Information about finishers unlocked by an account.") {
                CamelCase("id").."ID"(INTEGER, "the finisher's ID")
                "Permanent"(BOOLEAN, "whether or not the finisher is unlock permanently")
                optional.."Quantity"(INTEGER, "the remaining uses")
            }
        ))
    }
    "/Account/Gliders" {
        summary = "Returns information about a player's unlocked gliders."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each glider unlocked by the player"))
    }
    "/Account/Home" {
        summary = "Returns information about the available sub-endpoints."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.

        schema(array(STRING, "the available sub-endpoints"))
    }
    "/Account/Home/Nodes" {
        summary = "Returns information about a player's unlocked home instance nodes."
        security = setOf(ACCOUNT, PROGRESSION)

        schema(array(STRING, "an array of IDs containing th ID of each home instance node unlocked by the player"))
    }
    "/Account/Inventory" {
        summary = "Returns information about a player's shared inventory slots."
        security = setOf(ACCOUNT, INVENTORIES)

        schema(array(
            description = "A list of stacks of items in an account's shared inventory.",
            items = record(name = "AccountInventorySlot", description = "Information about a stack of items in a player's shared inventory.") {
                CamelCase("id").."ID"(INTEGER, "the item's ID")
                "Count"(INTEGER, "the amount of items in the stack")
                optional.."Charges"(INTEGER, "the amount of charges remaining on the item")
                optional.."Skin"(INTEGER, "the ID of the skin applied to the item")
                optional.."Upgrades"(array(INTEGER), "the array of item IDs of runes or sigils applied to the item")
                optional.."Infusions"(array(INTEGER), "the array of item IDs of infusions applied to the item")
                optional.."Stats"(
                    description = "information about the stats chosen for the item (if the item offers the option to select stats/prefix)",
                    type = record("Information about an item's stats.") {
                        CamelCase("id").."ID"(INTEGER, "the itemstat ID")
                        optional..SerialName("Power").."Power"(INTEGER, "the amount of power given by the item")
                        optional..SerialName("Precision").."Precision"(INTEGER, "the amount of precision given by the item")
                        optional..SerialName("Toughness").."Toughness"(INTEGER, "the amount of toughness given by the item")
                        optional..SerialName("Vitality").."Vitality"(INTEGER, "the amount of vitality given by the item")
                        optional..SerialName("ConditionDamage").."ConditionDamage"(INTEGER, "the amount of condition damage given by the item")
                        optional..SerialName("ConditionDuration").."ConditionDuration"(INTEGER, "the amount of condition duration given by the item")
                        optional..SerialName("Healing").."Healing"(INTEGER, "the amount of healing given by the item")
                        optional..SerialName("BoonDuration").."BoonDuration"(INTEGER, "the amount of boon duration given by the item")
                    }
                )
                optional.."Binding"(STRING, "the binding of the item")
            }
        ))
    }
    "/Account/Luck" {
        summary = "Returns information about a player's luck."
        security = setOf(ACCOUNT, PROGRESSION, UNLOCKS)

        schema(array(
            description = "the account's luck",
            items = record(name = "Luck", description = "Information about a plyer's luck.") {
                CamelCase("id").."ID"(STRING, "the type of luck (always \"luck\")")
                "Value"(INTEGER, "the amount of luck")
            }
        ))
    }
    "/Account/Mailcarriers" {
        summary = "Returns information about a player's unlocked mail carriers."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each mail carrier unlocked by the player"))
    }
    "/Account/MapChests" {
        summary = "Returns which Hero's Choice Chests a player has acquired since the most recent daily reset."
        security = setOf(ACCOUNT, PROGRESSION)

        schema(array(STRING, "an array of IDs for Hero's Choice Chests that the player has acquired since the most recent daily reset"))
    }
    "/Account/Masteries" {
        summary = "Returns information about a player's unlocked masteries."
        security = setOf(ACCOUNT, PROGRESSION)

        schema(array(
            description = "A list of all masteries unlocked by an account.",
            items = record(name = "AccountMastery", description = "Information about a player's unlocked mastery.") {
                CamelCase("id").."ID"(INTEGER, "the mastery's ID")
                optional.."Level"(INTEGER, "the index of the unlocked mastery level")
            }
        ))
    }
    "/Account/Mastery/Points" {
        summary = "Returns information about a player's unlocked mastery points."
        security = setOf(ACCOUNT, PROGRESSION)

        schema(record(name = "AccountMasteryPoint", description = "Information about a player's unlocked mastery points for a region.") {
            "Totals"(
                description = "information about the total mastery points for a region",
                type = array(record(description = "Information about the mastery points for a region.") {
                    "Region"(STRING, "the mastery region")
                    "Spent"(INTEGER, "the amount of mastery points of this region spent in mastery tracks")
                    "Earned"(INTEGER, "the amount of mastery points of this region earned for the account")
                })
            )
            "Unlocked"(array(INTEGER), "the list of IDs of unlocked mastery points")
        })
    }
    "/Account/Materials" {
        summary = "Returns information about the materials stored in a player's vault."
        security = setOf(ACCOUNT, INVENTORIES)

        schema(array(
            description = "A list of all materials in an account's vault.",
            items = record(name = "AccountMaterial", description = "Information about a stack of materials in a player's vault.") {
                CamelCase("id").."ID"(INTEGER, "the material's item ID")
                "Category"(INTEGER, "the material category the item belongs to")
                "Count"(INTEGER, "the number of the material that is stored in the player's vault")
                optional.."Binding"(STRING, "the binding of the material")
            }
        ))
    }
    "/Account/Minis" {
        summary = "Returns information about a player's unlocked miniatures."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each miniature unlocked by the player"))
    }
    "/Account/Mounts" {
        summary = "Returns information about the available sub-endpoints."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.

        schema(array(STRING, "the available sub-endpoints"))
    }
    "/Account/Mounts/Skins" {
        summary = "Returns information about a player's unlocked mount skins."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(STRING, "an array of IDs containing the ID of each mount skin unlocked by the player"))
    }
    "/Account/Mounts/Types" {
        summary = "Returns information about a player's unlocked mounts."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(STRING, "an array of IDs containing the ID of each mount unlocked by the player"))
    }
    "/Account/Novelties" {
        summary = "Returns information about a player's unlocked novelties."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each novelty unlocked by the player"))
    }
    "/Account/Outfits" {
        summary = "Returns information about a player's unlocked outfits."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each outfit unlocked by the player"))
    }
    "/Account/PvP/Heroes" {
        summary = "Returns information about a player's unlocked PvP heroes."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each PvP hero unlocked by the player"))
    }
    "/Account/Raids" {
        summary = "Returns which raid encounter a player has cleared since the most recent raid reset."
        security = setOf(ACCOUNT, PROGRESSION)

        schema(array(STRING, "an array of IDs containing the ID of each raid encounter that the player has cleared since the most recent raid reset"))
    }
    "/Account/Recipes" {
        summary = "Returns information about a player's unlocked recipes."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each recipe unlocked by the player"))
    }
    "/Account/Skins" {
        summary = "Returns information about a player's unlocked skins."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each skin unlocked by the player"))
    }
    "/Account/Titles" {
        summary = "Returns information about a player's unlocked titles."
        security = setOf(ACCOUNT, UNLOCKS)

        schema(array(INTEGER, "an array of IDs containing the ID of each title unlocked by the player"))
    }
    "/Account/Wallet" {
        summary = "Returns information about a player's wallet."
        security = setOf(ACCOUNT, WALLET)

        schema(array(
            description = "A list of all currencies in an account's wallet.",
            items = record(name = "AccountWalletCurrency", description = "Information about a currency in a player's wallet.") {
                CamelCase("id").."ID"(INTEGER, "the currency ID that can be resolved against /v2/currencies")
                "Value"(INTEGER, "the amount of this currency in the player's wallet")
            }
        ))
    }
    "/Account/WorldBosses" {
        summary = "Returns which world bosses that can be looted once per day a player has defeated since the most recent daily reset."
        security = setOf(ACCOUNT, PROGRESSION)

        schema(array(STRING, "an array of IDs for each world boss that can be looted once per day that the player has defeated since the most recent daily reset"))
    }
    "/Achievements" {
        summary = "Returns information about achievements."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS(all = false), BY_PAGE)
        schema(record(name = "Achievement", description = "Information about an achievement.") {
            CamelCase("id").."ID"(INTEGER, "the achievement's ID")
            optional.."Icon"(STRING, "the URL for the achievement's icon")
            "Name"(STRING, "the achievement's name")
            "Description"(STRING, "the achievement's description")
            "Requirement"(STRING, "the achievement's requirement as listed in-game")
            SerialName("locked_text").."LockedText"(STRING, "the achievement's in-game description prior to unlocking it")
            "Type"(STRING, "the achievement's type")
            "Flags"(array(STRING), "the achievement's categories")
            "Tiers"(
                description = "the achievement's tiers",
                type = array(record(name = "Tier", description = "Information about an achievement's tier.") {
                    "Count"(INTEGER, "the number of \"things\" (achievement-specific) that must be completed to achieve this tier")
                    "Points"(INTEGER, "the amount of AP awarded for completing this tier")
                })
            )
            optional.."Prerequisites"(array(INTEGER), "the IDs of the achievements that are required to progress this achievement")
            optional.."Rewards"(
                description = "the achievement's rewards",
                type = array(conditional(
                    name = "Reward",
                    description = "Information about an achievement reward.",
                    sharedConfigure = {
                        "Type"(STRING, "the type of reward")
                    }
                ) {
                    "Coins"(record("Information about a coin reward.") {
                        "Count"(INTEGER, "the amount of coins")
                    })
                    "Items"(record("Information about an item reward.") {
                        CamelCase("id").."ID"(INTEGER, "the item's ID")
                        "Count"(INTEGER, "the amount of the item")
                    })
                    "Mastery"(record("Information about a mastery point reward.") {
                        CamelCase("id").."ID"(INTEGER, "the mastery point's ID")
                        "Region"(STRING, "the mastery point's region")
                    })
                    "Title"(record("Information about a title reward") {
                        CamelCase("id").."ID"(INTEGER, "the title's ID")
                    })
                })
            )
            optional.."Bits"(
                description = "the achievement's bits",
                type = array(record(name = "Bit", description = "Information about an achievement bit.") {
                    "Type"(STRING, "the bit's type")
                    optional..CamelCase("id").."ID"(INTEGER, "the ID of the bit's object")
                    optional.."Text"(STRING, "the bit's text")
                })
            )
            optional..SerialName("point_cap").."PointCap"(INTEGER, "the maximum number of AP that can be rewarded by an achievement flagged as \"Repeatable\"")
        })
    }
    "/Achievements/Categories" {
        summary = "Returns information about achievement categories."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "AchievementCategory", description = "Information about an achievement category.") {
            CamelCase("id").."ID"(INTEGER, "the achievement category's ID")
            "Icon"(STRING, "the URL for the achievement category's icon")
            "Name"(STRING, "the achievement category's name")
            "Description"(STRING, "the achievement category's description")
            "Order"(INTEGER, "a number that can be used to sort the list of categories")
            "Achievements"(array(INTEGER), "an array containing the IDs of the achievements that this category contains")
        })
    }
    "/Achievements/Groups" {
        summary = "Returns information about achievement groups."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "AchievementGroups", description = "Information about an achievement group.") {
            CamelCase("id").."ID"(STRING, "the achievement group's ID")
            "Name"(STRING, "the achievement group's name")
            "Description"(STRING, "the achievement group's description")
            "Order"(INTEGER, "a number that can be used to sort the list of groups")
            "Categories"(array(INTEGER), "an array containing the IDs of the categories that this group contains")
        })
    }
    "/Backstory" {
        summary = "Returns information about the available sub-endpoints."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.

        schema(array(STRING, "the available sub-endpoints"))
    }
    "/Backstory/Answers" {
        summary = "Returns information about biography answers."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "BackstoryAnswer", description = "Information about a biography answer.") {
            CamelCase("id").."ID"(STRING, "the answer's ID")
            "Title"(STRING, "the answer's localized title")
            "Description"(STRING, "the answer's localized description")
            "Journal"(STRING, "the answer's journal entry")
            "Question"(INTEGER, "the ID of the biography question the answer answers")
            optional.."Professions"(array(STRING), "the IDs of the professions that this answer is available for")
            optional.."Races"(array(STRING), "the IDs of the races that this answer is available for")
        })
    }
    "/Build" {
        summary = "Returns the current build ID."

        schema(record(name = "Build", description = "Information about the current game build.") {
            CamelCase("id").."ID"(INTEGER, "the current build ID")
        })
    }
//    "/Characters" {
//        summary = ""
//        security = setOf(ACCOUNT, CHARACTERS)
//
//        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
//        schema(map {
//            "Name"(STRING, "the character's name")
//            "Race"(STRING, "the ID of the character's race")
//            "Gender"(STRING, "the character's gender")
//            "Flags"(INTEGER, "") // TODO array of something ?
//            "Profession"(STRING, "the ID of the character's profession")
//            "Level"(INTEGER, "the character's level")
//            "Guild"(STRING, "")
//        })
//    }
    "/Characters/:ID/Inventory" {
        summary = "Returns information about a character's inventory."
        security = setOf(ACCOUNT, CHARACTERS, INVENTORIES)

        pathParameter("ID", STRING, "the character's ID", camelCase = "id")
        schema(record(name = "CharactersInventorySlot", description = "Information about a bag in a character's inventory.") {
            "Bags"(
                description = "the character's inventory bags",
                type = array(record(name = "Bag", description = "Information about an inventory bag.") {
                    CamelCase("id").."ID"(INTEGER, "the bag's item ID")
                    "Size"(INTEGER, "the bag's size")
                    "Inventory"(
                        description = "the bag's content",
                        type = array(record(description = "Information about an item in a character's inventory.") {
                            CamelCase("id").."ID"(INTEGER, "the item's ID")
                            "Count"(INTEGER, "the amount of items in the stack")
                            optional.."Charges"(INTEGER, "the amount of charges remaining on the item")
                            optional.."Skin"(INTEGER, "the ID of the skin applied to the item")
                            optional.."Upgrades"(array(INTEGER), "an array of item IDs for each rune or signet applied to the item")
                            optional..SerialName("upgrade_slot_indices").."UpgradeSlotIndices"(array(INTEGER), "") // TODO description: figure out what this actually describes
                            optional.."Infusions"(array(INTEGER), "an array of item IDs for each infusion applied to the item")
                            optional.."Stats"(
                                description = "contains information on the stats chosen if the item offers an option for stats/prefix",
                                type = record(description = "Information about an item's stats.") {
                                    CamelCase("id").."ID"(INTEGER, "the itemstat ID")
                                    optional..SerialName("Power").."Power"(INTEGER, "the amount of power given by the item")
                                    optional..SerialName("Precision").."Precision"(INTEGER, "the amount of precision given by the item")
                                    optional..SerialName("Toughness").."Toughness"(INTEGER, "the amount of toughness given by the item")
                                    optional..SerialName("Vitality").."Vitality"(INTEGER, "the amount of vitality given by the item")
                                    optional..SerialName("ConditionDamage").."ConditionDamage"(INTEGER, "the amount of condition damage given by the item")
                                    optional..SerialName("ConditionDuration").."ConditionDuration"(INTEGER, "the amount of condition duration given by the item")
                                    optional..SerialName("Healing").."Healing"(INTEGER, "the amount of healing given by the item")
                                    optional..SerialName("BoonDuration").."BoonDuration"(INTEGER, "the amount of boon duration given by the item")
                                }
                            )
                            optional.."Binding"(STRING, "the binding of the material")
                            optional..SerialName("bound_to").."BoundTo"(STRING, "name of the character the item is bound to")
                        }, nullableItems = true)
                    )
                })
            )
        })
    }
    "/Colors" {
        summary = "Returns information about all dye colors in the game."
        cache = 1.hours
        isLocalized = true

        @APIGenDSL
        fun SchemaRecordBuilder.APPEARANCE() = record(description = "Information about the appearance of the color.") {
            "Brightness"(INTEGER, "the brightness")
            "Contrast"(DECIMAL, "the contrast")
            "Hue"(INTEGER, "the hue in HSL colorspace")
            "Saturation"(DECIMAL, "the saturation in HSL colorspace")
            "Lightness"(DECIMAL, "the lightness in HSL colorspace")
            "RGB"(array(INTEGER), "a list containing precalculated RGB values")
        }

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Color", description = "Information about a dye color.") {
            CamelCase("id").."ID"(INTEGER, "the color's ID")
            "Name"(STRING, "the color's name")
            SerialName("base_rgb").."BaseRGB"(array(INTEGER), "the base RGB values")
            "Cloth"(APPEARANCE(), "detailed information on its appearance when applied on cloth armor")
            "Leather"(APPEARANCE(), "detailed information on its appearance when applied on leather armor")
            "Metal"(APPEARANCE(), "detailed information on its appearance when applied on metal armor")
            optional.."Fur"(APPEARANCE(), "detailed information on its appearance when applied on fur armor")
            optional.."Item"(INTEGER, "the ID of the dye item")
            "Categories"(array(STRING), "the categories of the color")
        })
    }
    "/Commerce/Delivery" {
        summary = "Returns information about the items and coins currently available for pickup."
        security = setOf(ACCOUNT, TRADINGPOST)

        schema(record(name = "CommerceDelivery", description = "Information about the items and coins currently available for pickup.") {
            "Coins"(INTEGER, "the amount of coins ready for pickup")
            "Items"(
                description = "the items ready for pickup",
                type = array(record(name = "Item", description = "Information about an item ready for pickup.") {
                    CamelCase("id").."ID"(INTEGER, "the item's ID")
                    "Count"(INTEGER, "the amount of this item ready for pickup")
                })
            )
        })
    }
    "/Commerce/Exchange" {
        summary = "Returns information about the gem exchange."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.

        schema(array(STRING, "")) // TODO
    }
    "/Commerce/Exchange/:Type" {
        summary = "Returns information about the gem exchange."

        pathParameter("Type", STRING, "the exchange type")
        queryParameter("Quantity", INTEGER, "the amount to exchange")
        schema(record(name = "CommerceExchange", description = "Information about an exchange.") {
            SerialName("coins_per_gem").."CoinsPerGem"(INTEGER, "the number of coins received/required for a single gem")
            "Quantity"(INTEGER, "the number of coins/gems for received for the specified quantity of gems/coins")
        })
    }
    "/Commerce/Listings" {
        summary = "Returns current buy and sell listings from the trading post."

        @APIGenDSL
        fun SchemaRecordBuilder.LISTING(name: String) = record(name = name, description = "Information about an item's listing.") {
            "Listings"(INTEGER, "the number of individual listings this object refers to (e.g. two players selling at the same price will end up in the same listing)")
            SerialName("unit_price").."UnitPrice"(INTEGER, "the sell offer or buy order price in coins")
            "Quantity"(INTEGER, "the amount of items being sold/bought in this listing")
        }

        supportedQueries(BY_ID, BY_IDS(all = false), BY_PAGE)
        schema(record(name = "CommerceListing", description = "Information about an item listed in the trading post.") {
            CamelCase("id").."ID"(INTEGER, "the item's ID")
            "Buys"(array(LISTING(name = "Buy")), "list of all buy listings")
            "Sells"(array(LISTING(name = "Sell")), "list of all sell listings")
        })
    }
    "/Commerce/Prices" {
        summary = "Returns current aggregated buy and sell listing information from the trading post."

        supportedQueries(BY_ID, BY_IDS(all = false), BY_PAGE)
        schema(record(name = "CommercePrices", description = "Information about an item listed in the trading post.") {
            CamelCase("id").."ID"(INTEGER, "the item's ID")
            "Whitelisted"(BOOLEAN, "indicates whether or not a free to play account can purchase or sell this item on the trading post")
            "Buys"(
                description = "the buy information",
                type = record(description = "Information about an item's buy listing.") {
                    SerialName("unit_price").."UnitPrice"(INTEGER, "the highest buy order price in coins")
                    "Quantity"(INTEGER, "the amount of items being bought")
                }
            )
            "Sells"(
                description = "the sell information",
                type = record(description = "Information about an item's sell listing.") {
                    SerialName("unit_price").."UnitPrice"(INTEGER, "the lowest sell order price in coins")
                    "Quantity"(INTEGER, "the amount of items being sold")
                }
            )
        })
    }
    "/Commerce/Transactions" {
        summary = "Returns information about an account's transactions."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.
        security(ACCOUNT, TRADINGPOST)

        schema(array(STRING, "")) // TODO
    }
    "/Commerce/Transactions/:Relevance" {
        summary = "Returns information about an account's transactions."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.
        security(ACCOUNT, TRADINGPOST)

        pathParameter("Relevance", STRING, "the temporal relevance")
        schema(array(STRING, "")) // TODO
    }
    "/Commerce/Transactions/:Relevance/:Type" {
        summary = "Returns information about an account's transactions."
        cache = 1.minutes
        security(ACCOUNT, TRADINGPOST)

        pathParameter("Relevance", STRING, "the temporal relevance")
        pathParameter("Type", STRING, "the transaction type")
        supportedQueries(BY_PAGE)
        schema(record(name = "CommerceTransaction", description = "Information about a transaction.") {
            CamelCase("id").."ID"(INTEGER, "the transaction's ID")
            SerialName("item_id").."ItemID"(INTEGER, "the item's ID")
            "Price"(INTEGER, "the price in coins")
            "Quantity"(INTEGER, "the quantity of the item")
            "Created"(STRING, "the ISO-8601 standard timestamp of when the transaction was created")
            optional.."Purchased"(STRING, "the ISO-8601 standard timestamp of when the transaction was completed")
        })
    }
    "/Continents" {
        summary = "Returns information about continents."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Continent", description = "Information about a continent.") {
            CamelCase("id").."ID"(INTEGER, "the continent's ID")
            "Name"(STRING, "the continent's name")
            SerialName("continent_dims").."ContinentDims"(array(INTEGER), "the width and height of the continent")
            SerialName("min_zoom").."MinZoom"(INTEGER, "the minimal zoom level for use with the map tile service")
            SerialName("max_zoom").."MinZoom"(INTEGER, "the maximum zoom level for use with the map tile service")
            "Floors"(array(INTEGER), "the IDs of the continent's floors")
        })
    }
    "/CreateSubToken" {
        summary = "Creates a new subtoken."
        security(ACCOUNT)

        queryParameter("Expire", STRING, "an ISO-8601 datetime specifying when the generated subtoken will expire")
        queryParameter("Permissions", STRING, "a comma separated list of permissions to inherit")
        queryParameter("URLs", STRING, "a comma separated list of endpoints that will be accessible using this subtoken", isOptional = true, camelCase = "urls")
        schema(record(name = "SubToken", description = "A created subtoken.") {
            "Subtoken"(STRING, "a JWT which can be used like an API key")
        })
    }
    "/Currencies" {
        summary = "Returns information about currencies contained in the account wallet."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Currency", description = "Information about a currency.") {
            CamelCase("id").."ID"(INTEGER, "the currency's ID")
            "Name"(STRING, "the currency's name")
            "Description"(STRING, "a description of the currency")
            "Icon"(STRING, "the currency's icon")
            "Order"(INTEGER, "a number that can be used to sort the list of currencies")
        })
    }
    "/DailyCrafting" {
        summary = "Returns information about the items that can be crafted once per day."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "DailyCrafting", description = "Information about an item that can be crafted once per day.") {
            CamelCase("id").."ID"(STRING, "the ID of the dailycrafting")
        })
    }
    "/Dungeons" {
        summary = "Returns information about the dungeons in the game."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Dungeon", description = "Information about a dungeon.") {
            CamelCase("id").."ID"(STRING, "the dungeon's ID")
            "Paths"(
                description = "the dungeon's paths",
                type = array(record("Path", "Information about a dungeon path.") {
                    CamelCase("id").."ID"(STRING, "the path's ID")
                    "Type"(STRING, "the path's type")
                })
            )
        })
    }
    "/Emblem" {
        summary = "Returns information about guild emblem assets."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.

        schema(array(STRING, "the available sub-endpoints"))
    }
    "/Emblem/:Type" {
        summary = "Returns information about guild emblem assets."
        cache = 1.hours

        pathParameter("Type", STRING, "the layer for the emblem parts")
        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "EmblemPart", description = "Information about an emblem part.") {
            CamelCase("id").."ID"(STRING, "the emblem part's ID")
            "Layers"(array(STRING), "an array of URLs to images that make up the various parts of the emblem")
        })
    }
    "/Emotes" {
        summary = "Returns information about unlockable emotes."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Emote", description = "Information about an unlockable emote.") {
            CamelCase("id").."ID"(STRING, "the emote's ID")
            "Commands"(
                description = "the commands that may be used to trigger the emote",
                type = array(STRING)
            )
            SerialName("unlock_items").."UnlockItems"(
                description = "the IDs of the items that unlock the emote",
                type = array(INTEGER)
            )
        })
    }
    "/Files" {
        summary = "Returns commonly requested in-game assets."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "File", description = "Information about an in-game asset.") {
            CamelCase("id").."ID"(STRING, "the file identifier")
            "Icon"(STRING, "the URL to the image")
        })
    }
    "/Finishers" {
        summary = "Returns information about finishers."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Finisher", description = "Information about a finisher.") {
            CamelCase("id").."ID"(STRING, "the finisher's ID")
            "Name"(STRING, "the finisher's name")
            "Icon"(STRING, "the URL for the finisher's icon")
            "Order"(INTEGER, "a (non-unique) number that can be used as basis to sort the list of finishers")
            SerialName("unlock_details").."UnlockDetails"(STRING, "a description explaining how to acquire the finisher")
            SerialName("unlock_items").."UnlockItems"(array(INTEGER), "an array of item IDs used to unlock the finisher")
        })
    }
    "/Gliders" {
        summary = "Returns information about gliders."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Glider", description = "Information about a glider.") {
            CamelCase("id").."ID"(INTEGER, "the glider's ID")
            "Name"(STRING, "the glider's name")
            "Description"(STRING, "the glider's description")
            "Icon"(STRING, "the URL for the glider's icon")
            "Order"(INTEGER, "a (non-unique) number that can be used as basis to sort the list of gliders")
            SerialName("default_dyes").."DefaultDyes"(
                description = "the IDs of the dyes that are applied to the glider by default",
                type = array(INTEGER)
            )
        })
    }
    "/Guild/:ID" {
        summary = "Returns information about a guild."

        pathParameter("ID", STRING, "the guild's ID", camelCase = "id")
        schema(record(name = "Guild", description = "Information about a guild.") {
            CamelCase("id").."ID"(STRING, "the guild's ID")
            "Name"(STRING, "the guild's name")
            "Tag"(STRING, "the guild's tag")
            "Level"(INTEGER, "the guild's level")
            optional(GUILDS)..SerialName("motd").."MotD"(STRING, "the guild's message of the day")
            optional(GUILDS).."Influence"(INTEGER, "the guild's current influence")
            optional(GUILDS).."Aetherium"(INTEGER, "the guild's current aetherium")
            optional(GUILDS).."Favor"(INTEGER, "the guild's current favor")
            optional(GUILDS).."Resonance"(INTEGER, "the guild's current resonance")
            optional(GUILDS)..SerialName("member_count").."MemberCount"(INTEGER, "the guild's current member count")
            optional(GUILDS)..SerialName("member_capacity").."MemberCapacity"(INTEGER, "the guild's current member capacity")
            "Emblem"(
                description = "the guild's emblem",
                type = record(name = "Emblem", description = "") {
                    "Background"(
                        description = "the emblem's background",
                        type = record(name = "Background", description = "Information about a guild emblem's background.") {
                            CamelCase("id").."ID"(STRING, "the background's ID")
                            "Colors"(array(INTEGER), "the background's colors")
                        }
                    )
                    "Foreground"(
                        description = "the emblem's foreground",
                        type = record(name = "Foreground", description = "Information about a guild emblem's forground.") {
                            CamelCase("id").."ID"(STRING, "the foreground's ID")
                            "Colors"(array(INTEGER), "the foreground's colors")
                        }
                    )
                    "Flags"(array(STRING), "the manipulations applied to the emblem")
                }
            )
        })
    }
    "/Guild/:ID/Members" {
        summary = "Returns information about a guild's members."

        pathParameter("ID", STRING, "the guild's ID", camelCase = "id")
        schema(array(
            description = "the guild's members",
            items = record(name = "GuildMember", description = "Information about a guild member.") {
                "Name"(STRING, "the member's account name")
                "Rank"(STRING, "the member's rank")
                "Joined"(STRING, "the ISO8601 timestamp of when the member joined the guild")
            }
        ))
    }
    "/Guild/:ID/Ranks" {
        summary = "Returns information about a guild's ranks."

        pathParameter("ID", STRING, "the guild's ID", camelCase = "id")
        schema(array(
            description = "the guild's ranks",
            items = record(name = "GuildRank", description = "Information about a guild rank.") {
                CamelCase("id").."ID"(STRING, "the rank's ID")
                "Order"(INTEGER, "a number that can be used to sort the list of ranks")
                "Permissions"(array(STRING), "the rank's permissions")
                "Icon"(STRING, "a render service URL for the rank's icon")
            }
        ))
    }
    "/Guild/Permissions" {
        summary = "Returns information about available guild permissions."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "GuildPermission", description = "Information about a guild permission.") {
            CamelCase("id").."ID"(STRING, "the permission's ID")
            "Name"(STRING, "the permission's localized name")
            "Description"(STRING, "the permission's localized description")
        })
    }
    "/Guild/Upgrades" {
        summary = "Returns information about available guild hall upgrades."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(conditional(
            name = "GuildUpgrade",
            description = "Information about a guild upgrade.",
            sharedConfigure = {
                CamelCase("id").."ID"(INTEGER, "the upgrade's ID")
                "Name"(STRING, "the upgrade's name")
                "Description"(STRING, "the upgrade's description")
                "Type"(STRING, "the upgrade's type")
                "Icon"(STRING, "the URL for the upgrade's icon")
                SerialName("build_time").."BuildTime"(INTEGER, "the time it takes to build the upgrade")
                SerialName("required_level").."RequiredLevel"(INTEGER, "the prerequisite level the guild must be at to build the upgrade")
                "Experience"(INTEGER, "the amount of guild experience that will be awarded upon building the upgrade")
                "Prerequisites"(array(INTEGER), "an array of upgrade IDs that must be completed before this can be built")
                "Costs"(
                    description = "an array of objects describing the upgrade's cost",
                    type = array(record("Cost", "Information about an upgrade's cost.") {
                        "Type"(STRING, "the cost's type")
                        "Name"(STRING, "the cost's name")
                        "Count"(STRING, "the amount needed")
                        optional..SerialName("item_id").."ItemID"(INTEGER, "the ID of the cost's item")
                    })
                )
            }
        ) {
            "AccumulatingCurrency"(record("Information about a mine capacity upgrade.") {})
            "BankBag"(record("Information about a guild bank upgrades.") {
                SerialName("bag_max_items").."BagMaxItems"(INTEGER, "the maximum item slots of the guild bank tab")
                SerialName("bag_max_coins").."BagMaxCoins"(INTEGER, "the maximum amount of coins that can be stored in the bank tab")
            })
            "Boost"(record("Information about a permanent guild buffs upgrade.") {})
            "Claimable"(record("Information about a guild WvW tactics.") {})
            "Consumable"(record("Information about a banners and guild siege.") {})
            "Decoration"(record("Information about a decoration that must be crafted by a Scribe.") {})
            "GuildHall"(record("Information about claiming a Guild Hall.") {})
            "GuildHallExpedition"(record("Information about an expedition unlock.") {})
            "Hub"(record("Information about Guild Initiative office unlock.") {})
            "Queue"(record("Information about Workshop Restoration 1.") {})
            "Unlock"(record("Information about permanent unlocks, such as merchants and arena decorations.") {})
        })
    }
    "/Home" {
        summary = "Returns information about the available sub-endpoints."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.

        schema(array(STRING, "the available sub-endpoints"))
    }
    "/Home/Cats" {
        summary = "Returns information about home-instance cats."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "HomeInstanceCat", description = "Information about a home-instance cat.") {
            CamelCase("id").."ID"(INTEGER, "the cat's ID")
            "Hint"(STRING, "the unlock hint")
        })
    }
    "/Home/Nodes" {
        summary = "Returns information about home-instance nodes."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "HomeInstanceNode", description = "Information about a home-instance node.") {
            CamelCase("id").."ID"(STRING, "the node's ID")
        })
    }
    "/Items" {
        summary = "Returns information about items in the game."
        cache = 1.hours
        isLocalized = true

        @APIGenDSL
        fun SchemaRecordBuilder.INFIX_UPGRADE() = record(description = "Information about an item's infix upgrade.") {
            CamelCase("id").."ID"(INTEGER, "the itemstat ID")
            "Attributes"(
                description = "the list of attribute bonuses granted by this item",
                type = array(record(name = "Attribute", description = "Information about an infix upgrade's attribute bonuses.") {
                    "Attribute"(STRING, "the attribute this bonus applies to")
                    "Modifier"(INTEGER, "the modifier value")
                })
            )
            optional.."Buff"(
                description = "object containing an additional effect",
                type = record(description = "Information about an infix upgrade's buffs.") {
                    SerialName("skill_id").."SkillId"(INTEGER, "the skill ID of the effect")
                    optional.."Description"(STRING, "the effect's description")
                }
            )
        }

        @APIGenDSL
        fun SchemaRecordBuilder.INFUSION_SLOTS() = array(record(name = "InfusionSlot", description = "Information about an items infusion slot.") {
            "Flags"(array(STRING), "infusion slot type of infusion upgrades")
            optional..SerialName("item_id").."ItemId"(INTEGER, "the infusion upgrade in the armor piece")
        })

        supportedQueries(BY_ID, BY_IDS(all = false), BY_PAGE)
        schema(record(name = "Item", description = "Information about an item.") {
            CamelCase("id").."ID"(INTEGER, "the item's ID")
            "Name"(STRING, "the item's name")
            "Type"(STRING, "the item's type")
            SerialName("chat_link").."ChatLink"(STRING, "the chat link")
            optional.."Icon"(STRING, "the URL for the item's icon")
            optional.."Description"(STRING, "the item's description")
            "Rarity"(STRING, "the item's rarity")
            "Level"(INTEGER, "the level required to use the item")
            SerialName("vendor_value").."VendorValue"(INTEGER, "the value in coins when selling the item to a vendor")
            optional..SerialName("default_skin").."DefaultSkin"(INTEGER, "the ID of the item's default skin")
            "Flags"(array(STRING), "flags applying to the item")
            SerialName("game_types").."GameTypes"(array(STRING), "the game types in which the item is usable")
            "Restrictions"(array(STRING), "restrictions applied to the item")
            optional..SerialName("upgrades_into").."UpgradesInto"(
                description = "lists what items this item can be upgraded into, and the method of upgrading",
                type = array(record(description = "Information about an item's upgrade.") {
                    "Upgrade"(STRING, "describes the method of upgrading")
                    SerialName("item_id").."ItemId"(INTEGER, "the item ID that results from performing the upgrade")
                })
            )
            optional..SerialName("upgrades_from").."UpgradesFrom"(
                description = "lists what items this item can be upgraded from, and the method of upgrading",
                type = array(record(description = "Information about an item's precursor.") {
                    "Upgrade"(STRING, "describes the method of upgrading")
                    SerialName("item_id").."ItemId"(INTEGER, "the item ID that results from performing the upgrade")
                })
            )
            optional.."Details"(
                description = "additional information about the item",
                type = conditional(description = "Additional information about an item.", disambiguationBySideProperty = true) {
                    "Armor"(record(description = "Additional information about an armor item.") {
                        "Type"(STRING, "the armor slot type")
                        SerialName("weight_class").."WeightClass"(STRING, "the weight class")
                        "Defense"(INTEGER, "the defense value of the armor piece")
                        SerialName("infusion_slots").."InfusionSlots"(INFUSION_SLOTS(), "infusion slots of the armor piece")
                        optional..SerialName("infix_upgrade").."InfixUpgrade"(INFIX_UPGRADE(), "infix upgrade object")
                        optional..SerialName("suffix_item_id").."SuffixItemId"(INTEGER, "the suffix item ID")
                        until(V2_SCHEMA_2020_11_17T00_30_00_000Z)..optional..SerialName("secondary_suffix_item_id").."SecondarySuffixItemId"(STRING, "the secondary suffix item ID")
                        since(V2_SCHEMA_2020_11_17T00_30_00_000Z)..optional..SerialName("secondary_suffix_item_id").."SecondarySuffixItemId"(INTEGER, "the secondary suffix item ID")
                        optional..SerialName("stat_choices").."StatChoices"(array(INTEGER), "a list of selectable stat IDs which are visible in /v2/itemstats")
                        optional..SerialName("attribute_adjustment").."AttributeAdjustment"(DECIMAL, "") // TODO doc
                    })
                    "Back"(record(description = "Additional information about a backpiece.") {
                        SerialName("infusion_slots").."InfusionSlots"(INFUSION_SLOTS(), "infusion slots of the back item")
                        optional..SerialName("infix_upgrade").."InfixUpgrade"(INFIX_UPGRADE(), "infix upgrade object")
                        optional..SerialName("suffix_item_id").."SuffixItemId"(INTEGER, "the suffix item ID")
                        until(V2_SCHEMA_2020_11_17T00_30_00_000Z)..optional..SerialName("secondary_suffix_item_id").."SecondarySuffixItemId"(STRING, "the secondary suffix item ID")
                        since(V2_SCHEMA_2020_11_17T00_30_00_000Z)..optional..SerialName("secondary_suffix_item_id").."SecondarySuffixItemId"(INTEGER, "the secondary suffix item ID")
                        optional..SerialName("stat_choices").."StatChoices"(array(INTEGER), "a list of selectable stat IDs which are visible in /v2/itemstats")
                        optional..SerialName("attribute_adjustment").."AttributeAdjustment"(DECIMAL, "") // TODO doc
                    })
                    "Bag"(record(description = "Additional information about a bag.") {
                        "Size"(INTEGER, "the number of bag slots")
                        SerialName("no_sell_or_sort").."NoSellOrSort"(BOOLEAN, "whether the bag is invisible")
                    })
                    "Consumable"(record(description = "Additional information about a consumable item.") {
                        "Type"(STRING, "the consumable type")
                        optional.."Description"(STRING, "effect description for consumables applying an effect")
                        optional..SerialName("duration_ms").."DurationMs"(INTEGER, "effect duration in milliseconds")
                        optional..SerialName("unlock_type").."UnlockType"(STRING, "unlock type for unlock consumables")
                        optional..SerialName("color_id").."ColorId"(INTEGER, "the dye ID for dye unlocks")
                        optional..SerialName("recipe_id").."RecipeId"(INTEGER, "the recipe ID for recipe unlocks")
                        optional..SerialName("extra_recipe_ids").."ExtraRecipeIds"(array(INTEGER), "additional recipe IDs for recipe unlocks")
                        optional..SerialName("guild_upgrade_id").."GuildUpgradeId"(INTEGER, "the guild upgrade ID for the item")
                        optional..SerialName("apply_count").."ApplyCount"(INTEGER, "the number of stacks of the effect applied by this item")
                        optional.."Name"(STRING, "the effect type name of the consumable")
                        optional.."Icon"(STRING, "the icon of the effect")
                        optional.."Skins"(array(INTEGER), "a list of skin ids which this item unlocks")
                    })
                    "Container"(record(description = "Additional information about a container.") {
                        "Type"(STRING, "the container type")
                    })
                    "Gathering"(record(description = "Additional information about a gathering tool.") {
                        "Type"(STRING, "the tool type")
                    })
                    "Gizmo"(record(description = "Additional information about a gizmo.") {
                        "Type"(STRING, "the gizmo type")
                        optional..SerialName("guild_upgrade_id").."GuildUpgradeId"(INTEGER, "the guild upgrade ID for the item")
                        optional..SerialName("vendor_ids").."VendorIds"(array(INTEGER), "the vendor IDs")
                    })
                    "MiniPet"(record(description = "Additional information about a mini unlock item.") {
                        SerialName("minipet_id").."MinipetId"(INTEGER, "the miniature it unlocks")
                    })
                    "Tool"(record(description = "Additional information about a tool.") {
                        "Type"(STRING, "the tool type")
                        "Charges"(INTEGER, "the available charges")
                    })
                    "Trinket"(record(description = "Additional information about a trinket.") {
                        "Type"(STRING, "the trinket type")
                        SerialName("infusion_slots").."InfusionSlots"(INFUSION_SLOTS(), "infusion slots of the trinket")
                        optional..SerialName("infix_upgrade").."InfixUpgrade"(INFIX_UPGRADE(), "infix upgrade object")
                        optional..SerialName("suffix_item_id").."SuffixItemId"(INTEGER, "the suffix item ID")
                        until(V2_SCHEMA_2020_11_17T00_30_00_000Z)..optional..SerialName("secondary_suffix_item_id").."SecondarySuffixItemId"(STRING, "the secondary suffix item ID")
                        since(V2_SCHEMA_2020_11_17T00_30_00_000Z)..optional..SerialName("secondary_suffix_item_id").."SecondarySuffixItemId"(INTEGER, "the secondary suffix item ID")
                        optional..SerialName("stat_choices").."StatChoices"(array(INTEGER), "a list of selectable stat IDs which are visible in /v2/itemstats")
                        optional..SerialName("attribute_adjustment").."AttributeAdjustment"(DECIMAL, "") // TODO doc
                    })
                    "UpgradeComponent"(record(description = "Additional information about an upgrade component.") {
                        "Type"(STRING, "the type of the upgrade component")
                        "Flags"(array(STRING), "the items that can be upgraded with the upgrade component")
                        SerialName("infusion_upgrade_flags").."InfusionUpgradeFlags"(array(STRING), "applicable infusion slot for infusion upgrades")
                        "Suffix"(STRING, "the suffix appended to the item name when the component is applied")
                        optional..SerialName("infix_upgrade").."InfixUpgrade"(INFIX_UPGRADE(), "infix upgrade object")
                        optional.."Bonuses"(array(STRING), "the bonuses from runes")
                        optional..SerialName("attribute_adjustment").."AttributeAdjustment"(DECIMAL, "") // TODO doc
                    })
                    "Weapon"(record(description = "Additional information about a weapon.") {
                        "Type"(STRING, "the weapon type")
                        SerialName("min_power").."MinPower"(INTEGER, "minimum weapon strength")
                        SerialName("max_power").."MaxPower"(INTEGER, "maximum weapon strength")
                        SerialName("damage_type").."DamageType"(STRING, "the damage type")
                        "Defense"(INTEGER, "the defense value of the weapon")
                        SerialName("infusion_slots").."InfusionSlots"(INFUSION_SLOTS(), "infusion slots of the weapon")
                        optional..SerialName("infix_upgrade").."InfixUpgrade"(INFIX_UPGRADE(), "infix upgrade object")
                        optional..SerialName("suffix_item_id").."SuffixItemId"(INTEGER, "the suffix item ID")
                        until(V2_SCHEMA_2020_11_17T00_30_00_000Z)..optional..SerialName("secondary_suffix_item_id").."SecondarySuffixItemId"(STRING, "the secondary suffix item ID")
                        since(V2_SCHEMA_2020_11_17T00_30_00_000Z)..optional..SerialName("secondary_suffix_item_id").."SecondarySuffixItemId"(INTEGER, "the secondary suffix item ID")
                        optional..SerialName("stat_choices").."StatChoices"(array(INTEGER), "a list of selectable stat IDs which are visible in /v2/itemstats")
                        optional..SerialName("attribute_adjustment").."AttributeAdjustment"(DECIMAL, "") // TODO doc
                    })
                }
            )
        })
    }
    "/ItemStats" {
        summary = "Returns information about itemstats."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "ItemStatSet", description = "Information about a stat set.") {
            CamelCase("id").."ID"(INTEGER, "the stat set's ID")
            "Name"(STRING, "the name of the stat set")
            "Attributes"(
                description = "the list of attribute bonuses",
                type = array(record(name = "Attribute", description = "Information about an attribute bonus.") {
                    "Attribute"(STRING, "the name of the attribute")
                    "Multiplier"(DECIMAL, "the multiplier for that attribute")
                    "Value"(INTEGER, "the default value for that attribute")
                })
            )
        })
    }
    "/Legends" {
        summary = "Returns information about the Revenant legends."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Legend", description = "Information about a Revenant legend.") {
            CamelCase("id").."ID"(STRING, "the legend's ID")
            "Swap"(INTEGER, "the ID of the profession (swap Legend) skill")
            "Heal"(INTEGER, "the ID of the heal skill")
            "Elite"(INTEGER, "the ID of the elite skills")
            "Utilities"(
                description = "the IDs of the utility skills",
                type = array(INTEGER)
            )
        })
    }
    "/Mailcarriers" {
        summary = "Returns information about mailcarriers."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Mailcarrier", description = "Information about a mailcarrier.") {
            CamelCase("id").."ID"(INTEGER, "the mailcarrier's ID")
            "Icon"(STRING, "the URL for the mailcarrier's icon")
            "Name"(STRING, "the mailcarrier's name")
            "Order"(INTEGER, "a number that can be used to sort the list of mailcarriers")
            SerialName("unlock_items").."UnlockItems"(array(INTEGER), "an array containing the IDs of the items used to unlock the mailcarrier")
            "Flags"(array(STRING), "additional flags describing the mailcarrier")
        })
    }
    "/MapChests" {
        summary = "Returns information about the Hero's Choice Chests that can be acquired once per day."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "MapChest", description = "Information about a Hero's Choice Chests that can be acquired once per day.") {
            CamelCase("id").."ID"(STRING, "the ID of the chest")
        })
    }
    "/Maps" {
        summary = "Returns information about maps."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Map", description = "Information about a map.") {
            CamelCase("id").."ID"(INTEGER, "the map's ID")
            "Name"(STRING, "the map's name")
            "Type"(STRING, "the type of map")
            SerialName("min_level").."MinLevel"(INTEGER, "the minimum level of the map")
            SerialName("max_level").."MaxLevel"(INTEGER, "the maximum level of the map")
            SerialName("default_floor").."DefaultFloor"(INTEGER, "the ID of the map's default floor")
            "Floors"(array(INTEGER), "the IDs of the floors available on the map")
            SerialName("region_id").."RegionID"(INTEGER, "the ID of the region the map belongs to")
            optional..SerialName("region_name").."RegionName"(STRING, "the name of the region the map belongs to")
            SerialName("continent_id").."ContinentID"(INTEGER, "the ID of the continent the map belongs to")
            optional..SerialName("continent_name").."ContinentName"(STRING, "the name of the continent the map belongs to")
            SerialName("map_rect").."MapRect"(array(array(INTEGER)), "the dimensions of the map, given as the coordinates of the lower-left (SW) and upper-right (NE) corners")
            SerialName("continent_rect").."ContinentRect"(array(array(INTEGER)), "the dimensions of the map within the continent coordinate system, given as the coordinates of the upper-left (NW) and lower-right (SE) corners")
        })
    }
    "/Masteries" {
        summary = "Returns information about masteries."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Mastery", description = "Information about a mastery.") {
            CamelCase("id").."ID"(INTEGER, "the mastery's ID")
            "Name"(STRING, "the mastery's name")
            "Requirement"(STRING, "the written out requirement to unlock the mastery track")
            "Order"(INTEGER, "the order in which the mastery track appears in a list")
            "Background"(STRING, "the URL for the mastery track's background graphic")
            "Region"(STRING, "the mastery region the track belongs to")
            "Levels"(
                description = "information about each mastery level",
                type = array(record(name = "Level", description = "Information about a mastery level.") {
                    "Name"(STRING, "the mastery level's name")
                    "Description"(STRING, "the mastery level's description")
                    "Instruction"(STRING, "the in-game instruction for the mastery level")
                    "Icon"(STRING, "the URL for the mastery level's icon")
                    SerialName("point_cost").."PointCost"(INTEGER, "the amount of mastery points required to unlock the level")
                    SerialName("exp_cost").."ExpCost"(INTEGER, "the amount of experience required to unlock the level")
                })
            )
        })
    }
    "/Materials" {
        summary = "Returns information about the categories in the material storage."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "MaterialCategory", description = "Information about a material category.") {
            CamelCase("id").."ID"(INTEGER, "the category's ID")
            "Name"(STRING, "the category's name")
            "Items"(array(INTEGER), "the IDs of this category's items")
            "Order"(INTEGER, "the category's sorting key")
        })
    }
    "/Minis" {
        summary = "Returns information about minis."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Mini", description = "Information about a mini.") {
            CamelCase("id").."ID"(STRING, "the mini's ID")
            "Name"(STRING, "the mini's name")
            optional.."Description"(STRING, "the description of how to unlock the mini")
            "Icon"(STRING, "the URL for the mini's icon")
            "Order"(INTEGER, "a (non-unique) number that can be used as basis to sort the list of minis")
            SerialName("item_id").."ItemID"(STRING, "the ID of the item which unlocks the mini")
        })
    }
    "/Mounts" {
        summary = "Returns information about the available sub-endpoints."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.

        schema(array(STRING, "the available sub-endpoints"))
    }
    "/Mounts/Skins" {
        summary = "Returns information about mount skins."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "MountSkin", description = "Information about a mount skin.") {
            CamelCase("id").."ID"(INTEGER, "the mount skin's ID")
            "Name"(STRING, "the mount skin's name")
            "Icon"(STRING, "a render service URL for the mount skin's icon")
            "Mount"(STRING, "the mount type id for the mount skin")
            SerialName("dye_slots").."DyeSlots"(
                description = "the mount skin's dye slots",
                type = array(record(name = "DyeSlot", description = "Information about a dye slot.") {
                    SerialName("color_id").."ColorID"(STRING, "the ID of the color")
                    "Material"(STRING, "the slot's material")
                })
            )
        })
    }
    "/Mounts/Types" {
        summary = "Returns information about mount types."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "MountType", description = "Information about a mount type.") {
            CamelCase("id").."ID"(STRING, "the mount type's ID")
            "Name"(STRING, "the mount type's name")
            SerialName("default_skin").."DefaultSkin"(INTEGER, "the ID of the mount type's default skin")
            "Skins"(array(INTEGER), "the IDs of the skins available for the mount type")
            "Skills"(
                description = "the mount type's skills",
                type = array(record(name = "Skill", description = "Information about a mount skill.") {
                    CamelCase("id").."ID"(INTEGER, "the mount skill's ID")
                    "Slot"(STRING, "the mount skill's slot")
                })
            )
        })
    }
    "/Novelties" {
        summary = "Returns information about novelties."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Novelty", description = "Information about a novelty.") {
            CamelCase("id").."ID"(INTEGER, "the novelty's ID")
            "Name"(STRING, "the novelty's name")
            "Icon"(STRING, "a render service URL for the novelty's icon")
            "Description"(STRING, "the novelty's description")
            "Slot"(STRING, "the novelty's slot")
            SerialName("unlock_item").."UnlockItems"(
                description = "the IDs of the items that unlock the novelty",
                type = array(INTEGER)
            )
        })
    }
    "/Outfits" {
        summary = "Returns information about outfits."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Outfit", description = "Information about an outfit.") {
            CamelCase("id").."ID"(INTEGER, "the outfit's ID")
            "Name"(STRING, "the outfit's name")
            "Icon"(STRING, "the outfit's icon")
            SerialName("unlock_items").."UnlockItems"(
                description = "the IDs of the items that unlock the outfit",
                type = array(INTEGER)
            )
        })
    }
    "/Pets" {
        summary = "Returns information about pets."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Pet", description = "Information about a pet.") {
            CamelCase("id").."ID"(STRING, "the pet's ID")
            "Name"(STRING, "the pet's name")
            "Description"(STRING, "the pet's description")
            "Icon"(STRING, "a render service URL for the pet's icon")
            "Skills"(
                description = "the pet's skills",
                type = array(record(name = "Skill", description = "Information about a pet's skill.") {
                    CamelCase("id").."ID"(STRING, "the skill's ID")
                })
            )
        })
    }
    "/Professions" {
        summary = "Returns information about the game's playable professions."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Profession", description = "Information about a playable profession.") {
            CamelCase("id").."ID"(STRING, "the profession's ID")
            "Name"(STRING, "the profession's localized name")
            since(V2_SCHEMA_2019_12_19T00_00_00_000Z).."Code"(INTEGER, "the profession's palette code")
            "Icon"(STRING, "a render service URL for the profession's icon")
            SerialName("icon_big").."BigIcon"(STRING, "a render service URL for a big version of the profession's icon")
            "Specializations"(array(INTEGER), "the IDs of the profession's specializations")
            "Weapons"(
                description = "information about the weapons usable by this profession",
                type = map(
                    keys = STRING,
                    values = record(name = "Weapon", description = "Information about a profession's weapon and it's skills.") {
                        optional.."Specialization"(INTEGER, "the ID of the profession's specializations required for this weapon")
                        "Flags"(array(STRING), "additional flags describing this weapon's properties (e.g. MainHand, OffHand, TwoHand, Aquatic)")
                        "Skills"(
                            description = "the skills for the weapon if wielded by this profession",
                            type = array(record(name = "Skill", description = "Information about a weapon's skills.") {
                                CamelCase("id").."ID"(INTEGER, "the skill's ID")
                                "Slot"(STRING, "the skill's slot")
                                optional.."Attunement"(STRING, "the elementalist attunement for this skill")
                                optional.."Offhand"(STRING, "the offhand weapon for this skill")
                            })
                        )
                    }
                )
            )
            "Flags"(array(STRING), "additional flags describing this profession's properties (e.g. NoRacialSkills)")
            "Skills"(
                description = "the profession specific skills",
                type = array(record(name = "Skill", description = "Information about a profession skill.") {
                    CamelCase("id").."ID"(INTEGER, "the skill's ID")
                    "Slot"(STRING, "the skill's slot")
                    "Type"(STRING, "the skill's type")
                    optional.."Attunement"(STRING, "the elementalist attunement for this skill")
                    optional.."Source"(STRING, "the profession ID of the source of the stolen skill") // TODO is this correct?
                })
            )
            "Training"(
                description = "array of trainings of this profession",
                type = array(record(description = "Information about training track.") {
                    CamelCase("id").."ID"(INTEGER, "the training's ID")
                    "Category"(STRING, "the training's category")
                    "Name"(STRING, "the training's localized name")
                    "Track"(
                        description = "array of skill/trait in the training track",
                        type = array(record(description = "Information about a skill/trait in a track.") {
                            "Cost"(INTEGER, "the amount of skill points required to unlock this step")
                            "Type"(STRING, "the type of the step (e.g. Skill, Trait)")
                            optional..SerialName("skill_id").."SkillId"(INTEGER, "the ID of the skill unlocked by this step")
                            optional..SerialName("trait_id").."TraitId"(INTEGER, "the ID of the trait unlocked by this step")
                        })
                    )
                })
            )
            since(V2_SCHEMA_2019_12_19T00_00_00_000Z)..SerialName("skills_by_palette").."SkillsByPalette"(
                description = "mappings from palette IDs to skill IDs",
                type = array(array(INTEGER))
            )
        })
    }
    "/PvP" {
        summary = "Returns information about the available sub-endpoints."
        cache = DURATION_INFINITE // We don't expect this to change. Ever.

        schema(array(STRING, "the available sub-endpoints"))
    }
    "/PvP/Heroes" {
        summary = "Returns information about the PvP heroes."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "PvPHero", description = "Information about a PvP hero.") {
            CamelCase("id").."ID"(STRING, "the PvP hero's ID")
            "Name"(STRING, "the hero's localized name")
            "Type"(STRING, "the flavor type describing the hero")
            "Stats"(
                description = "the hero's stats",
                type = record(name = "Stats", description = "Information about a hero's stats.") {
                    "Offense"(INTEGER, "the offense stat")
                    "Defense"(INTEGER, "the defense stat")
                    "Speed"(INTEGER, "the speed stat")
                }
            )
            "Overlay"(STRING, "the render service URL for the hero's overlay art")
            "Underlay"(STRING, "the render service URL for the hero's underlay art")
            "Skins"(
                description = "the hero's skins",
                type = array(record(name = "Skin", description = "Information about a PvP hero's skin.") {
                    CamelCase("id").."ID"(INTEGER, "the PvP hero skin's ID")
                    "Name"(STRING, "the hero skin's localized name")
                    "Icon"(STRING, "a render service URL for the skin's icon")
                    "Default"(BOOLEAN, "whether or not the skin is the champion's default skin")
                    SerialName("unlock_items").."UnlockItems"(array(INTEGER), "an array of item IDs used to unlock the finisher")
                })
            )
        })
    }
    "/PvP/Ranks" {
        summary = "Returns information about the PvP ranks."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "PvPRank", description = "Information about a PvP rank.") {
            CamelCase("id").."ID"(INTEGER, "the PvP rank's ID")
            SerialName("finisher_id").."FinisherID"(INTEGER, "the rank finisher's ID")
            "Name"(STRING, "the rank's localized name")
            "Icon"(STRING, "a render service URL for the rank's icon")
            SerialName("min_rank").."MinRank"(INTEGER, "the minimum PvP level required for the rank")
            SerialName("max_rank").."MaxRank"(INTEGER, "the maximum PvP level for the rank")
            "Levels"(
                description = "the rank's levels",
                type = array(record(name = "Level", description = "Information about a PvP rank's level.") {
                    SerialName("min_rank").."MinRank"(INTEGER, "the minimum PvP level required for the level")
                    SerialName("max_rank").."MaxRank"(INTEGER, "the maximum PvP level for the level")
                    "Points"(INTEGER, "the amount of PvP experience needed to go from the given minimum rank to maximum rank")
                })
            )
        })
    }
    "/Quaggans" {
        summary = "Returns images of quaggans."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Finisher", description = "Information about a finisher.") {
            CamelCase("id").."ID"(STRING, "the quaggans's ID")
            CamelCase("URL").."URL"(STRING, "the URL to the quaggan image")
        })
    }
    "/Quests" {
        summary = "Returns information about Story Journal missions."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Quest", description = "Information about a quest.") {
            CamelCase("id").."ID"(STRING, "the quest's ID")
            "Name"(STRING, "the quest's localized name")
            "Level"(INTEGER, "the minimum level required to begin the quest")
            "Story"(INTEGER, "the story's ID")
            "Goals"(
                description = "the quest's goals",
                type = array(record(name = "Goal", description = "Information about a quest's goal.") {
                    "Active"(STRING, "the text displayed for the quest step if it is active")
                    "Complete"(STRING, "the text displayed for the quest step if it is complete")
                })
            )
        })
    }
    "/Races" {
        summary = "Returns information about the game's playable races."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Race", description = "Information about a playable race.") {
            CamelCase("id").."ID"(STRING, "the race's ID")
            "Name"(STRING, "the race's localized name")
            "Skills"(array(INTEGER), "an array of racial skill IDs")
        })
    }
    "/Raids" {
        summary = "Returns information about the raids in the game."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Raid", description = "Information about a raid.") {
            CamelCase("id").."ID"(STRING, "the raid's ID")
            "Wings"(
                description = "the raid's wings",
                type = array(record("Wing", "Information about a wing.") {
                    CamelCase("id").."ID"(STRING, "the wing's ID")
                    "Events"(
                        description = "the wing's events",
                        type = array(record("Event", "Information about an event.") {
                            CamelCase("id").."ID"(STRING, "the event's ID")
                            "Type"(STRING, "the event's type")
                        })
                    )
                })
            )
        })
    }
    "/Recipes" {
        summary = "Returns information about the crafting recipes in the game."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS(all = false), BY_PAGE)
        schema(record(name = "Recipe", description = "Information about a crafting recipe.") {
            CamelCase("id").."ID"(STRING, "the recipe's ID")
            "Type"(STRING, "the recipe's type")
            SerialName("output_item_id").."OutputItemID"(INTEGER, "the ID of the produced item")
            SerialName("output_item_count").."OutputItemCount"(INTEGER, "the amount of items produced")
            SerialName("time_to_craft_ms").."CraftTimeMillis"(INTEGER, "the time in milliseconds it takes to craft the item")
            "Disciplines"(array(STRING), "the crafting disciplines that can use the recipe")
            SerialName("min_rating").."MinRating"(INTEGER, "the minimum rating required to use the recipe")
            "Flags"(array(STRING), "the flags applying to the recipe")
            "Ingredients"(
                description = "the recipe's ingredients",
                type = array(record(name = "Ingredient", description = "Information about a recipe ingredient.") {
                    SerialName("item_id").."ItemID"(STRING, "the ingredient's item ID")
                    "Count"(INTEGER, "the quantity of this ingredient")
                })
            )
            optional..SerialName("guild_ingredients").."GuildIngredients"(
                description = "the recipe's guild ingredients",
                type = array(record(name = "Ingredient", description = "Information about a recipe guild ingredient.") {
                    SerialName("upgrade_id").."UpgradeID"(STRING, "the guild ingredient's guild upgrade ID")
                    "Count"(INTEGER, "the quantity of this guild ingredient")
                })
            )
            optional..SerialName("output_upgrade_id").."OutputUpgradeID"(INTEGER, "the ID of the produced guild upgrade")
            SerialName("chat_link").."ChatLink"(STRING, "the recipe's chat code")
        })
    }
    "/Specializations" {
        summary = "Returns information about the specializations in the game."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Specialization", description = "Information about a specialization.") {
            CamelCase("id").."ID"(INTEGER, "the ID of the specialization")
            "Name"(STRING, "the localized name of the specialization")
            "Profession"(STRING, "the ID of the profession the specialization belongs to")
            "Elite"(BOOLEAN, "a flag indicating whether or not the specialization is an elite specialization")
            "Icon"(STRING, "a render service URL for the specialization's icon")
            "Background"(STRING, "a render service URL for the specialization's background image")
            SerialName("minor_traits").."MinorTraits"(array(INTEGER), "a list of all IDs of the specialization's minor traits")
            SerialName("major_traits").."MajorTraits"(array(INTEGER), "a list of all IDs of the specialization's major traits")
            optional..SerialName("weapon_trait").."WeaponTrait"(INTEGER, "the ID of the elite specialization's weapon trait")
            optional..SerialName("profession_icon").."ProfessionIcon"(STRING, "a render service URL for the elite specialization's icon")
            optional..SerialName("profession_icon_big").."BigProfessionIcon"(STRING, "a render service URL for a large variant of the elite specialization's icon")
        })
    }
    "/Stories" {
        summary = "Returns information about the Story Journal stories."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "StorySeason", description = "Information about a Story Journal season.") {
            CamelCase("id").."ID"(INTEGER, "the ID of the story")
            "Season"(STRING, "the ID of the story's season")
            "Name"(STRING, "the localized name of the story")
            "Description"(STRING, "the localized description of the story")
            "Timeline"(STRING, "the in-game date of the story")
            "Level"(INTEGER, "the minimum level required to start to begin the story")
            "Order"(INTEGER, "a number that can be used to sort the list of stories")
            "Chapters"(
                description = "the story's chapters",
                type = array(record(name = "Chapter", description = "Information about a story chapter.") {
                    "Name"(STRING, "the localized name of the chapter")
                })
            )
            optional.."Races"(array(STRING), "the races eligible to participate in the story")
            optional.."Flags"(array(STRING), "additional requirements for a character to participate in the story")
        })
    }
    "/Stories/Seasons" {
        summary = "Returns information about the Story Journal seasons."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "StorySeason", description = "Information about a Story Journal season.") {
            CamelCase("id").."ID"(STRING, "the ID of the season")
            "Name"(STRING, "the localized name of the season")
            "Order"(INTEGER, "a number that can be used to sort the list of seasons")
            "Stories"(array(INTEGER), "the IDs of the stories in the season")
        })
    }
    "/Titles" {
        summary = "Returns information about the titles that are in the game."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "Title", description = "Information about a title.") {
            CamelCase("id").."ID"(INTEGER, "the ID of the title")
            "Name"(STRING, "the display name of the title")
            deprecated..optional.."Achievement"(INTEGER, "the ID of the achievement that grants this title")
            optional.."Achievements"(array(INTEGER), "the IDs of the achievements that grant this title")
            optional..SerialName("ap_required")..CamelCase("apRequired").."APRequired"(INTEGER, "the amount of AP required to unlock this title")
        })
    }
    "/TokenInfo" {
        summary = "Returns information about the supplied API key."
        security(ACCOUNT)

        schema(
            V2_SCHEMA_CLASSIC to record(name = "TokenInfo", description = "Information about an API key.") {
                CamelCase("id").."ID"(STRING, "the API key that was requested")
                "Name"(STRING, "the name given to the API key by the account owner")
                "Permissions"(
                    description = "an array of strings describing which permissions the API key has",
                    type = array(STRING)
                )
            },
            V2_SCHEMA_2019_05_22T00_00_00_000Z to record(name = "TokenInfo", description = "Information about an API key.") {
                CamelCase("id").."ID"(STRING, "the API key that was requested")
                "Name"(STRING, "the name given to the API key by the account owner")
                "Permissions"(
                    description = "an array of strings describing which permissions the API key has",
                    type = array(STRING)
                )
                "Type"(STRING, "the type of the access token given")
                optional..SerialName("expires_at").."ExpiresAt"(STRING, "if a subtoken is given, ISO8601 timestamp indicating when the given subtoken expires")
                optional..SerialName("issued_at").."IssuedAt"(STRING, "if a subtoken is given, ISO8601 timestamp indicating when the given subtoken was created")
                optional..CamelCase("urls").."URLs"(array(STRING), "an array of strings describing what endpoints are available to this token (if the given subtoken is restricted to a list of URLs)")
            }
        )
    }
    "/WorldBosses" {
        summary = "Returns information about the worldbosses that reward boss chests that can be opened once a day."
        cache = 1.hours

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "WorldBoss", description = "Information about a worldboss that reward boss chests that can be opened once a day.") {
            CamelCase("id").."ID"(STRING, "the worldboss's ID")
        })
    }
    "/Worlds" {
        summary = "Returns information about the available worlds (or servers)."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "World", description = "Information about an available world (or server).") {
            CamelCase("id").."ID"(INTEGER, "the ID of the world")
            "Name"(STRING, "the name of the world")
            "Population"(STRING, "the population level of the world")
        })
    }
    "/WvW/Abilities" {
        summary = "Returns information about the achievable ranks in the World versus World game mode."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "WvWAbility", description = "Information about an ability in the World versus World game mode.") {
            CamelCase("id").."ID"(INTEGER, "the ID of the ability")
            "Name"(STRING, "the ability's name")
            "Description"(STRING, "the ability's description")
            "Icon"(STRING, "a render service URL for the mount ability's icon")
            "Ranks"(
                description = "the ability's ranks",
                type = array(record(name = "Rank", description = "Information about an ability's rank.") {
                    "Cost"(INTEGER, "the WvW experience points required to unlock the rank")
                    "Effect"(STRING, "the rank's effect")
                })
            )
        })
    }
    "/WvW/Objectives" {
        summary = "Returns information about the objectives in the World versus World game mode."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "WvWObjective", description = "Information about an objective in the World versus World game mode.") {
            CamelCase("id").."ID"(STRING, "the ID of the objective")
            "Name"(STRING, "the name of the objective")
            "Type"(STRING, "the type of the objective")
            SerialName("sector_id").."SectorId"(INTEGER, "the map sector the objective can be found in")
            SerialName("map_id").."MapId"(INTEGER, "the ID of the map the objective can be found on")
            SerialName("map_type").."MapType"(STRING, "the type of the map the objective can be found on")
            optional.."Coord"(array(DECIMAL), "an array of three numbers representing the X, Y and Z coordinates of the objectives marker on the map")
            optional..SerialName("label_coord").."LabelCoord"(array(DECIMAL), "an array of two numbers representing the X and Y coordinates of the sector centroid")
            optional.."Marker"(STRING, "the icon link")
            SerialName("chat_link").."ChatLink"(STRING, "the chat code for the objective")
            optional..SerialName("upgrade_id").."UpgradeId"(INTEGER, "the ID of the upgrades available for the objective")
        })
    }
    "/WvW/Ranks" {
        summary = "Returns information about the achievable ranks in the World versus World game mode."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "WvWRank", description = "Information about an achievable rank in the World versus World game mode.") {
            CamelCase("id").."ID"(INTEGER, "the ID of the rank")
            "Title"(STRING, "the title of the rank")
            SerialName("min_rank").."MinRank"(INTEGER, "the WvW level required to unlock this rank")
        })
    }
    "/WvW/Upgrades" {
        summary = "Returns information about available upgrades for objectives in the World versus World game mode."
        cache = 1.hours
        isLocalized = true

        supportedQueries(BY_ID, BY_IDS, BY_PAGE)
        schema(record(name = "WvWUpgrade", description = "Information about an upgrade for objectives in the World versus World game mode.") {
            CamelCase("id").."ID"(INTEGER, "the ID of the upgrade")
            "Tiers"(
                description = "the different tiers of the upgrade",
                type = array(record(description = "Information about an upgrade tier.") {
                    "Name"(STRING, "the name of the upgrade tier")
                    SerialName("yaks_required").."YaksRequired"(INTEGER, "the amount of dolyaks required to reach this upgrade tier")
                    "Upgrades"(
                        description = "the upgrades available at the tier",
                        type = array(record(description = "Information about an upgrade.") {
                            "Name"(STRING, "the name of the upgrade")
                            "Description"(STRING, "the description for the upgrade")
                            "Icon"(STRING, "the icon link")
                        })
                    )
                })
            )
        })
    }
}