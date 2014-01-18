__author__ = 'zhengjitang@gmail.com'

import pickle
import uuid
import string
import binascii

import hashlib
import random
from Crypto import Random
from Crypto.Cipher import AES


class Encrypt:
    def __init__(self, key):
        self.key = key

    def encode(self, obj):
        def pad(s):
            x = AES.block_size - len(s) % AES.block_size
            return s + (bytes([x]) * x)

        padded = pad(pickle.dumps(obj))

        iv = Random.OSRNG.posix.new().read(AES.block_size)
        cipher = AES.new(self.key, AES.MODE_CBC, iv)

        return binascii.hexlify(iv + cipher.encrypt(padded))

    def decode(self, binary):
        code = binascii.unhexlify(binary)
        un_pad = lambda s: s[:-s[-1]]
        iv = code[:AES.block_size]
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return pickle.loads(un_pad(cipher.decrypt(code))[AES.block_size:])

    @staticmethod
    def md5(obj):
        return hashlib.md5(pickle.dumps(obj)).hexdigest()

    @staticmethod
    def sha512(obj):
        return hashlib.sha512(pickle.dumps(obj)).hexdigest()

    @staticmethod
    def uuid():
        return uuid.uuid4().hex

    @staticmethod
    def random_str(length):
        return ''.join(random.sample(string.ascii_uppercase + string.digits, length))
