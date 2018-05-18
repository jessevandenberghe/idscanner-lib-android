package be.appwise.idscanner.helpers

import android.util.Log
import be.appwise.idscanner.models.IDScan
import java.util.*

class CharacterConverterHelper {
	
	companion object {
		
		private val tag = CharacterConverterHelper::class.java.simpleName
		
		private fun convertToCharacters(numbers: String) : String{
			var characters = numbers.replace('0','O')
			characters =  characters.replace('1','I')
			characters =  characters.replace('2','Z')
			characters =  characters.replace('3','B')
			characters =  characters.replace('4','A')
			characters =  characters.replace('5','S')
			characters =  characters.replace('8','B')
			return characters
		}
		
		private fun convertToNumbers(characters: String) : String{
			var numbers =  characters.replace('O','0')
			numbers = numbers.replace('D','0')
			numbers = numbers.replace(' ','0')
			numbers = numbers.replace('I','1')
			numbers = numbers.replace('Z','2')
			numbers = numbers.replace('A','4')
			numbers = numbers.replace('L','4')
			numbers = numbers.replace('S','5')
			numbers = numbers.replace('Y','7')
			numbers = numbers.replace('B','8')
			numbers = numbers.replace('Q','9')
			return numbers
		}
		
		fun convertIdCode(code: String): String{
			val rules = code.split('\n')
			var output = ""
			
			try {
				output += convertToCharacters(rules[0].substring(0, 5))
				output += convertToNumbers(rules[0].substring(5, rules[0].length))
				output += '\n'
				
				output += convertToNumbers(rules[1].substring(0, 7))
				output += convertToGender(convertToCharacters(rules[1].substring(7, 8)))
				output += convertToNumbers(rules[1].substring(8, 15))
				output += convertToCharacters(rules[1].substring(15, 18))
				output += convertToNumbers(rules[1].substring(18, rules[1].length))
				output += '\n'
				
				output += convertToCharacters(rules[2])
			}
			catch (e: StringIndexOutOfBoundsException){ }
			finally {
				return output
			}
		}
		
		fun convertToId(code: String) : IDScan?{
			val identity = IDScan()
			val rules = code.split( '\n' )
			
			try {
				val debugStart = System.currentTimeMillis()
				when {
					rules[0].length !in 30 .. 31 -> throw Exception( "rulelength1 " + rules[0].length + " != 30" )
					rules[1].length !in 30 .. 31 -> throw Exception( "rulelength2 " + rules[1].length + " != 30" )
					rules[2].length !in 30 .. 31 -> throw Exception( "rulelength3 " + rules[2].length + " != 30" )
				}
				rules[0].replaceRange( 29 , 30 , "<" )
				rules[0].trim()
				identity.nationality = rules[0].substring(2, 5)
				identity.cardNumber = rules[0].substring(5, rules[0].length).replace("<", "")
				val birthDate = GregorianCalendar(convertYearEndToPastInt(rules[1].substring(0, 2)), rules[1].substring(2, 4).toInt() - 1, rules[1].substring(4, 6).toInt()).time
				identity.birthDate = birthDate
				identity.gender = rules[1].substring(7, 8)
				val expireDate = GregorianCalendar(convertYearEndToFutureInt(rules[1].substring(8, 10)), rules[1].substring(10, 12).toInt() - 1, rules[1].substring(12, 14).toInt()).time
				identity.validUntil = expireDate
				identity.registerID = rules[1].substring(18, rules[1].length).replace("<", "")
				val fullname = rules[2].split("<<")
				identity.lastName = fullname[0].replace('<', ' ')
				identity.firstName = fullname[1].split('<')[0]
				return identity
			}
			catch (e: Exception){
				Log.e(tag, "convertToId: " + e.message)
				return null
			}
		}
		
		private fun convertToGender(gender: String): String{
			return  gender.replace("H", "M")
		}
		
		private fun convertYearEndToPastInt(year: String): Int{
			var intYear = year.toInt()
			intYear += if(intYear > 18){
				1900
			} else{
				2000
			}
			return intYear
		}
		
		private fun convertYearEndToFutureInt(year: String): Int{
			return year.toInt() + 2000
		}
		
	}
}