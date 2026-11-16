import importlib.util
import unittest
from pathlib import Path


spec = importlib.util.spec_from_file_location(
    "dark_android_release", Path(__file__).parents[1] / "scripts/dark_android_release.py"
)
release = importlib.util.module_from_spec(spec)
spec.loader.exec_module(release)


class DarkAndroidReleaseTest(unittest.TestCase):
    def test_versions_increase_beyond_the_original_apk(self):
        first = release.metadata("v0.1.0-alpha", 1)
        later = release.metadata("v0.2.0-alpha.2", 9)
        self.assertEqual(first["version"], "0.1.0-alpha")
        self.assertGreater(first["version_code"], 154)
        self.assertGreater(later["version_code"], first["version_code"])

    def test_only_prerelease_tags_are_accepted(self):
        for tag in ["v1.0.0", "main", "v01.0.0-alpha", "v1.0.0-alpha\nother=value", "v1.0.0-$(id)"]:
            with self.subTest(tag=tag), self.assertRaises(ValueError):
                release.metadata(tag, 1)

    def test_version_code_bounds_are_enforced(self):
        for number in [0, -1, 2100000000]:
            with self.subTest(number=number), self.assertRaises(ValueError):
                release.metadata("v0.1.0-alpha", number)

    def test_wrong_or_multiple_signers_are_rejected(self):
        for certificates in [[], ["0" * 64], [release.CERT_SHA256, "0" * 64]]:
            with self.subTest(certificates=certificates), self.assertRaises(ValueError):
                release.check_certificate(certificates)

    def test_apk_identity_and_version_must_match(self):
        text = "package: name='io.github.xenbw.dark.debug' versionCode='155' versionName='0.1.0-alpha'\napplication-label:'DARK'\n"
        release.check_badging(text, "0.1.0-alpha", 155)
        for wrong in [text.replace("dark.debug", "other"), text.replace("155", "154"), text.replace("0.1.0-alpha", "14.0.0")]:
            with self.subTest(wrong=wrong), self.assertRaises(ValueError):
                release.check_badging(wrong, "0.1.0-alpha", 155)


if __name__ == "__main__":
    unittest.main()
