__author__ = 'zhengjitang@gmail.com'

import pickle
import uuid
import string
import binascii

import hashlib
import random
from Crypto import Random
from Crypto.Cipher import AES, Blowfish


class Encrypt:
    def __init__(self, key):
        self.key = key

    def encode(self, obj):
        def pad(s):
            x = AES.block_size - len(s) % AES.block_size
            return s + (bytes([x]) * x)

        padded = pad(pickle.dumps((obj, Encrypt.random_str(8))))

        iv = Random.OSRNG.posix.new().read(AES.block_size)
        cipher = AES.new(self.key, AES.MODE_CBC, iv)

        return binascii.hexlify(iv + cipher.encrypt(padded))

    def decode(self, binary):
        code = binascii.unhexlify(binary)
        un_pad = lambda s: s[:-s[-1]]
        iv = code[:AES.block_size]
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        obj, salt = pickle.loads(un_pad(cipher.decrypt(code))[AES.block_size:])
        return obj

    @staticmethod
    def password(plain):
        slat = Encrypt.random_str(16)
        key = Encrypt.random_str(16)
        sha = hashlib.md5(plain.encode()).hexdigest()
        #logging.error("加密 %s"%sha)
        sha = Blowfish.new(key).encrypt(sha)
        sha = binascii.hexlify(sha).decode().upper()
        #logging.error("%s %s %s " % (key, slat, sha))
        return "%s%s%s" % (key, sha, slat)

    @staticmethod
    def check(plain, password):
        key = password[0:16]
        sha = password[16:16 + 64]
        #logging.error("%s %s"%(key, sha))
        sha = binascii.unhexlify(sha.lower().encode())
        sha = Blowfish.new(key).decrypt(sha).decode()
        #logging.error("解密 %s"%sha)
        return hashlib.md5(plain.encode()).hexdigest() == sha

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
    def random_str(length, chars=string.ascii_uppercase + string.digits):
        return ''.join([random.choice(chars) for i in range(0, length)])
        #return ''.join(random.sample(string.ascii_uppercase + string.digits, length))
